package uz.sh;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultVoice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shoxruh Bekpulatov
 * Time : 08/02/23
 */
@Slf4j
@Component
public class BoomOvozBot extends TelegramLongPollingBot {

    private final AudioRepo audioRepo;
    private final AdminsRepo adminsRepo;


    public BoomOvozBot(AudioRepo audioRepo, AdminsRepo adminsRepo) {
        this.audioRepo = audioRepo;
        this.adminsRepo = adminsRepo;
    }

    @Override
    public String getBotUsername() {
        return "boomovozbot";
    }

    @Override
    public String getBotToken() {
        return "6456408777:AAFX0aQMjBb6Nbzde3g5y4FM1BFzGxSbym4";
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("New update : {}", update.toString());
        if (Objects.nonNull(update.getMessage())) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            if (chatId.equals(5185166129L) && message.hasVoice() && Objects.nonNull(message.getCaption())) {

                String name = " ";
                try {
                    name = message.getCaption().substring(0, message.getCaption().indexOf("\n"));
                } catch (Exception e) {
                    name = message.getCaption();
                }
                Voice voice = message.getVoice();
                if (audioRepo.findByAudioName(name).isEmpty()) {
                    Audio save = audioRepo.save(new Audio(null, name, voice.getFileId(), voice.getFileUniqueId(), voice.getDuration(), voice.getMimeType(), voice.getFileSize()));
                    executeMSG(chatId.toString(), "Shep qowildi " + save.toString());

                } else
                    executeMSG(chatId.toString(), "Shep oldindan bor ekan bu " + name);

            } else if (isAdmin(chatId) && message.hasVoice()) {
                Voice voice = message.getVoice();
                Audio audio =
                        new Audio(null, null, voice.getFileId(), voice.getFileUniqueId(), voice.getDuration(), voice.getMimeType(), voice.getFileSize());
                Audio save = audioRepo.save(audio);
                executeMSG(chatId.toString(), "Shep qowildi " + save.toString());
            }
            if (chatId.equals(5185166129L) && message.hasText() && message.getText().startsWith("SHDADMIN")) {
                String adminChatId = message.getText().substring(8, message.getText().indexOf("#"));
                String adminName = message.getText().substring(message.getText().indexOf("#") + 1);
                Admins admins = adminsRepo.save(new Admins(null, adminName, adminChatId));
                executeMSG(chatId.toString(), "Admin saved with : " + admins.toString());
            } else if (isAdmin(chatId)) {
                if (message.hasText() && message.getText().startsWith("SHD")) {
                    String name = message.getText().substring(3);
                    audioRepo.updateLastAudioName(name);
                }
            }
            if (message.hasText() && message.getText().startsWith("SHD myChatId")) {
                executeMSG(chatId.toString(), "Your chatId : " + chatId);
            }


            executeMSG(chatId.toString(), "Uzbekistan is equals to Uzbekistan");
        }
        if (update.hasInlineQuery()) {
            InlineQuery inlineQuery = update.getInlineQuery();
            String query = inlineQuery.getQuery();
            List<InlineQueryResultVoice> shox = audioRepo
                    .findByNameLikeIgnoreCase(query)
                    .stream()
                    .map(m -> new InlineQueryResultVoice(m.getFileUniqueId(), m.getFileId(), m.getAudioName()))
                    .limit(20)
                    .collect(Collectors.toList());
            this.answerInlineQuery(inlineQuery.getId(), shox);
        }
    }

    public void answerInlineQuery(@NonNull String inlineQueryId, @NonNull List<InlineQueryResultVoice> results) {
        AnswerInlineQuery message = AnswerInlineQuery
                .builder()
                .inlineQueryId(inlineQueryId)
                .results(results)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isAdmin(Long id) {
        return adminsRepo.findByChatId(id.toString()).isPresent();
    }

    private void executeMSG(String chatId, String message) {
        try {
            execute(new SendMessage(chatId, message));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}

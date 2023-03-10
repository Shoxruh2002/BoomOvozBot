package uz.sh;

import lombok.NonNull;
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

/**
 * @author Shoxruh Bekpulatov
 * Time : 08/02/23
 */
@Component
public class BoomOvozBot extends TelegramLongPollingBot {

    private final AudioRepo audioRepo;
    private final AdminsRepo adminsRepo;


    public BoomOvozBot( AudioRepo audioRepo, AdminsRepo adminsRepo ) {
        this.audioRepo = audioRepo;
        this.adminsRepo = adminsRepo;
    }

    @Override
    public String getBotUsername() {
        return "BoomOvozBot";
    }

    @Override
    public String getBotToken() {
        return "6011310726:AAFuKTD9_OTankKvIacn32AlJOuoDE14moQ";
    }

    @Override
    public void onUpdateReceived( Update update ) {
        if ( Objects.nonNull(update.getMessage()) ) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            if ( chatId.equals(1038332919L) && message.hasVoice() && Objects.nonNull(message.getCaption()) ) {
                String name = message.getCaption().substring(0, message.getCaption().indexOf("\n"));
                Voice voice = message.getVoice();
                Audio save = audioRepo.save(new Audio(null, name, voice.getFileId(), voice.getFileUniqueId(), voice.getDuration(), voice.getMimeType(), voice.getFileSize()));

                executeMSG(chatId.toString(), "Shep qowildi " + save.toString());
            } else if ( isAdmin(chatId) && message.hasVoice() ) {
                Voice voice = message.getVoice();
                Audio audio =
                        new Audio(null, null, voice.getFileId(), voice.getFileUniqueId(), voice.getDuration(), voice.getMimeType(), voice.getFileSize());
                Audio save = audioRepo.save(audio);
                executeMSG(chatId.toString(), "Shep qowildi " + save.toString());
            }
            if ( chatId.equals(1038332919L) && message.hasText() && message.getText().startsWith("SHDADMIN") ) {
                String adminChatId = message.getText().substring(8, message.getText().indexOf("#"));
                String adminName = message.getText().substring(message.getText().indexOf("#") + 1);
                Admins admins = adminsRepo.save(new Admins(null, adminName, adminChatId));
                executeMSG(chatId.toString(), "Admin saved with : " + admins.toString());
            } else if ( isAdmin(chatId) ) {
                if ( message.hasText() && message.getText().startsWith("SHD") ) {
                    String name = message.getText().substring(3);
                    audioRepo.updateLastAudioName(name);
                }
            }
            if ( message.hasText() && message.getText().startsWith("SHD myChatId") ) {
                executeMSG(chatId.toString(), "Your chatId : " + chatId);
            }


            executeMSG(chatId.toString(), "Uzbekistan is equals to Uzbekistan");
        }
        if ( update.hasInlineQuery() ) {
            InlineQuery inlineQuery = update.getInlineQuery();
            String query = inlineQuery.getQuery();
            List<InlineQueryResultVoice> shox = audioRepo
                    .findByNameLikeIgnoreCase(query)
                    .stream()
                    .map(m -> new InlineQueryResultVoice(m.getFileUniqueId(), m.getFileId(), m.getAudioName()))
                    .toList();
            this.answerInlineQuery(inlineQuery.getId(), shox);
        }
    }

    public void answerInlineQuery( @NonNull String inlineQueryId, @NonNull List<InlineQueryResultVoice> results ) {
        AnswerInlineQuery message = AnswerInlineQuery
                .builder()
                .inlineQueryId(inlineQueryId)
                .results(results)
                .build();
        try {
            execute(message);
        } catch ( TelegramApiException e ) {
            e.printStackTrace();
        }
    }

    public boolean isAdmin( Long id ) {
        return adminsRepo.findByChatId(id.toString()).isPresent();
    }

    private void executeMSG( String chatId, String message ) {
        try {
            execute(new SendMessage(chatId, message));
        } catch ( TelegramApiException e ) {
            throw new RuntimeException(e);
        }
    }
}

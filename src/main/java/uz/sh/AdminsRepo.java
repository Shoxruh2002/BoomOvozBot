package uz.sh;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Shoxruh Bekpulatov
 * Time : 09/02/23
 */
public interface AdminsRepo extends JpaRepository<Admins, Long> {

    Optional<Admins> findByChatId( String chatId );
}

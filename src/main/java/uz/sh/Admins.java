package uz.sh;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Shoxruh Bekpulatov
 * Time : 09/02/23
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Admins {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String chatId;

}

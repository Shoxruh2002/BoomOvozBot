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
 * Time : 08/02/23
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String audioName;

    private String fileId;

    private String fileUniqueId;

    private long duration;

    private String mimeType;

    private Long size;

}

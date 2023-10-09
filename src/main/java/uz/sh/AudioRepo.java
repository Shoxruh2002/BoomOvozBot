package uz.sh;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Shoxruh Bekpulatov
 * Time : 08/02/23
 */
public interface AudioRepo extends JpaRepository<Audio, Long> {

    @Query(value = "select a.* from public.audio a where a.audio_name ilike concat('%',:name,'%') ", nativeQuery = true)
    List<Audio> findByNameLikeIgnoreCase( @Param("name") String name );


    Optional<Audio> findByAudioName( String audioName );
    @Modifying
    @Transactional
    @Query(value = "update public.audio  set audio_name = :name where id in (select aa.id from audio aa order by aa.id desc limit 1 )", nativeQuery = true)
    void updateLastAudioName( @Param("name") String name );


}

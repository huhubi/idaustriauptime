package example.com.idauptime;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, Long> {
    List<ServiceStatus> findByNameOrderByTimestampDesc(String name);
}
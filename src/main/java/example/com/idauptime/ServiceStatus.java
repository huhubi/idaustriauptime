package example.com.idauptime;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class ServiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ServiceState currentState;

    private LocalDateTime timestamp;

    public ServiceStatus() {}

    public ServiceStatus(String name, ServiceState currentState) {
        this.name = name;
        this.currentState = currentState;
        this.timestamp = LocalDateTime.now();
    }

}
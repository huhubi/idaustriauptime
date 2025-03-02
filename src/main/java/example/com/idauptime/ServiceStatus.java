package example.com.idauptime;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_status")
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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ServiceState getCurrentState() {
        return currentState;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
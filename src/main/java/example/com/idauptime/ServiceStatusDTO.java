package example.com.idauptime;
import java.time.LocalDateTime;

public class ServiceStatusDTO {

    private String name;
    private String currentState; // Keep as String for proper mapping
    private LocalDateTime timestamp;

    public ServiceStatusDTO(String name, String currentState, LocalDateTime timestamp) {
        this.name = name;
        this.currentState = currentState;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getCurrentState() {
        return currentState;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
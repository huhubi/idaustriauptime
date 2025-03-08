package example.com.idauptime;
import java.time.LocalDateTime;

public class ServiceStatusDTO {

    private String name;
    private String currentState;
    private LocalDateTime timestamp;
    private LocalDateTime lastIncidentTimestamp; // Last NOT OK timestamp
    private String lastIncidentType; // Last incident type (WARNING or UNDEFINED)

    public ServiceStatusDTO(String name, String currentState, LocalDateTime timestamp, LocalDateTime lastIncidentTimestamp, String lastIncidentType) {
        this.name = name;
        this.currentState = currentState;
        this.timestamp = timestamp;
        this.lastIncidentTimestamp = lastIncidentTimestamp;
        this.lastIncidentType = lastIncidentType;
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

    public LocalDateTime getLastIncidentTimestamp() {
        return lastIncidentTimestamp;
    }

    public String getLastIncidentType() {
        return lastIncidentType;
    }
}
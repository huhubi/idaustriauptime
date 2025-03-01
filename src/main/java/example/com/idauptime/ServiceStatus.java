package example.com.idauptime;

import java.time.LocalDateTime;

public class ServiceStatus {
    private String name;
    private ServiceState currentState;
    private LocalDateTime timestamp;

    public ServiceStatus(String name, ServiceState currentState) {
        this.name = name;
        this.currentState = currentState;
        this.timestamp = LocalDateTime.now();
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
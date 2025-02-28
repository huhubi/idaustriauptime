package example.com.idauptime;
import lombok.Setter;

import java.time.LocalDateTime;

public class ServiceStatus {
    private String name;
    @Setter
    private String currentStatus;
    @Setter
    private LocalDateTime timestampDegradation;
    @Setter
    private LocalDateTime timestampRestoration;

    public ServiceStatus(String name) {
        this.name = name;
        this.currentStatus = "Unknown";
    }

    public String getName() {
        return name;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public LocalDateTime getTimestampDegradation() {
        return timestampDegradation;
    }

    public LocalDateTime getTimestampRestoration() {
        return timestampRestoration;
    }

}

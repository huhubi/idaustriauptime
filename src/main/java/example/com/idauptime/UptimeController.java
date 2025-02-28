package example.com.idauptime;

import example.com.idauptime.UptimeService;
import example.com.idauptime.ServiceStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class UptimeController {

    private final UptimeService uptimeService;

    public UptimeController(UptimeService uptimeService) {
        this.uptimeService = uptimeService;
    }

    @GetMapping
    public Map<String, ServiceStatus> getUptimeStatus() {
        return uptimeService.getCurrentServiceStatus();
    }
}
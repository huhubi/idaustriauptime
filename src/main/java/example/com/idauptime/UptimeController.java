package example.com.idauptime;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/uptime")
public class UptimeController {

    private final UptimeService uptimeService;

    public UptimeController(UptimeService uptimeService) {
        this.uptimeService = uptimeService;
    }

    @GetMapping
    public List<ServiceStatus> getUptimeHistory() {
        return uptimeService.getAllStatusChanges();
    }
}
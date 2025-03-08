package example.com.idauptime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
@Controller
public class UptimeController {
    private final UptimeService uptimeService;

    public UptimeController(UptimeService uptimeService) {
        this.uptimeService = uptimeService;
    }

    @GetMapping("/")
    public String showStatus(Model model) {
        List<ServiceStatus> statuses = uptimeService.getAllStatusChanges();
        model.addAttribute("statuses", statuses);
        return "index";
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        List<ServiceStatus> history = uptimeService.getAllStatusChanges();
        model.addAttribute("history", history);
        return "history";
    }
}
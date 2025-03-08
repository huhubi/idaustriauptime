package example.com.idauptime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UptimeController {
    private final ServiceStatusRepository serviceStatusRepository;

    public UptimeController(UptimeService uptimeService, ServiceStatusRepository serviceStatusRepository) {
        this.serviceStatusRepository = serviceStatusRepository;
    }

    @GetMapping("/")
    public String showStatus(Model model) {
        List<ServiceStatus> allStatuses = serviceStatusRepository.findAll();

        // Group by service name and get the latest status for each service
        Map<String, ServiceStatus> latestStatuses = allStatuses.stream()
                .collect(Collectors.toMap(
                        ServiceStatus::getName,
                        status -> status,
                        (existing, replacement) -> existing.getTimestamp().isAfter(replacement.getTimestamp()) ? existing : replacement
                ));

        model.addAttribute("statuses", latestStatuses);
        return "index";
    }

    @GetMapping("/history/{service}")
    public String showHistory(@PathVariable String service, Model model) {
        List<ServiceStatus> history = serviceStatusRepository.findByNameOrderByTimestampDesc(service);
        model.addAttribute("service", service);
        model.addAttribute("history", history);
        return "history";
    }
}

package example.com.idauptime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UptimeController {
    private final JpaServiceStatusRepository serviceStatusRepository;

    public UptimeController(JpaServiceStatusRepository serviceStatusRepository) {
        this.serviceStatusRepository = serviceStatusRepository;
    }

    @GetMapping("/")
    public String showStatus(Model model) {
        Map<String, List<ServiceStatus>> serviceStatuses = Map.of(
                "Anmeldung im Web mit ID Austria", serviceStatusRepository.findLatestStatusForService("login_web_service_status"),
                "Aktivierung der App \"Digitales Amt\"", serviceStatusRepository.findLatestStatusForService("activate_app_service_status"),
                "Anmeldung mit der App \"Digitales Amt\"", serviceStatusRepository.findLatestStatusForService("login_app_service_status"),
                "Deaktivierung der App \"Digitales Amt\"", serviceStatusRepository.findLatestStatusForService("deactivate_app_service_status"),
                "eIDAS Knoten", serviceStatusRepository.findLatestStatusForService("eidas_service_status")
        );

        // Debugging: Print values
        serviceStatuses.forEach((key, value) -> {
            System.out.println("Service: " + key);
            value.forEach(status -> System.out.println("  - " + status.getName() + ": " + status.getCurrentState()));
        });

        model.addAttribute("serviceStatuses", serviceStatuses);
        return "index";
    }

    @GetMapping("/history/{service}")
    public String showHistory(@PathVariable String service, Model model) {
        List<ServiceStatus> history = serviceStatusRepository.findHistoryForService(service);
        model.addAttribute("service", service);
        model.addAttribute("history", history);
        return "history";
    }

}
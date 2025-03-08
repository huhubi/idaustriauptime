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

        Map<String, Double> uptimePercentages = serviceStatuses.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateUptime(entry.getValue())
                ));

        model.addAttribute("serviceStatuses", serviceStatuses);
        model.addAttribute("uptimePercentages", uptimePercentages);
        return "index";
    }

    @GetMapping("/history/{service}")
    public String showHistory(@PathVariable String service, Model model) {
        List<ServiceStatus> history = serviceStatusRepository.findHistoryForService(service);
        Map<String, Double> uptimeStats = calculateUptimeForPeriods(history);

        model.addAttribute("service", service);
        model.addAttribute("history", history);
        model.addAttribute("uptimeStats", uptimeStats);
        return "history";
    }

    private double calculateUptime(List<ServiceStatus> statuses) {
        if (statuses.isEmpty()) return 100.0;

        long totalRecords = statuses.size();
        long downtimeRecords = statuses.stream()
                .filter(s -> s.getCurrentState() == ServiceState.WARNING || s.getCurrentState() == ServiceState.UNDEFINED)
                .count();

        return ((totalRecords - downtimeRecords) * 100.0) / totalRecords;
    }

    private Map<String, Double> calculateUptimeForPeriods(List<ServiceStatus> statuses) {
        if (statuses.isEmpty()) {
            return Map.of("last7Days", 100.0, "last30Days", 100.0, "overall", 100.0);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        List<ServiceStatus> last7Days = statuses.stream()
                .filter(s -> s.getTimestamp().isAfter(sevenDaysAgo))
                .toList();

        List<ServiceStatus> last30Days = statuses.stream()
                .filter(s -> s.getTimestamp().isAfter(thirtyDaysAgo))
                .toList();

        double uptime7Days = calculateUptime(last7Days);
        double uptime30Days = calculateUptime(last30Days);
        double overallUptime = calculateUptime(statuses);

        return Map.of("last7Days", uptime7Days, "last30Days", uptime30Days, "overall", overallUptime);
    }
}
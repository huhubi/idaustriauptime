package example.com.idauptime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class UptimeController {

    private final JpaServiceStatusRepository serviceStatusRepository;

    public UptimeController(JpaServiceStatusRepository serviceStatusRepository) {
        this.serviceStatusRepository = serviceStatusRepository;
    }

    @GetMapping("/")
    public String showServiceStatus(Model model) {
        List<ServiceStatusDTO> statuses = serviceStatusRepository.findLatestStatusForAllServices();
        model.addAttribute("statuses", statuses);
        return "status";
    }
}
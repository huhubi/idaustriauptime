package example.com.idauptime;

import org.springframework.boot.web.servlet.error.ErrorController;
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

    @GetMapping("/") // Ensures the root path loads the Thymeleaf template
    public String showServiceStatus(Model model) {
        List<ServiceStatusDTO> statuses = serviceStatusRepository.findLatestStatusForAllServices();
        model.addAttribute("statuses", statuses);
        return "status"; // This loads src/main/resources/templates/status.html
    }

    @Controller
    public class CustomErrorController implements ErrorController {

        @GetMapping("/error")
        public String handleError() {
            return "error"; // This will serve error.html
        }
    }
}
package example.com.idauptime;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.transaction.annotation.Transactional;
@Service

    public class UptimeService {

        private final ServiceStatusRepository serviceStatusRepository;
        private static final String STATUS_PAGE_URL = "http://127.0.0.1:5000";

        private final Map<String, ServiceState> lastKnownStates = new ConcurrentHashMap<>();

        private static final List<String> TRACKED_SERVICES = List.of(
                "Anmeldung im Web mit ID Austria",
                "Aktivierung der App \"Digitales Amt\"",
                "Anmeldung mit der App \"Digitales Amt\"",
                "Deaktivierung der App \"Digitales Amt\"",
                "eIDAS Knoten"
        );

        public UptimeService(ServiceStatusRepository serviceStatusRepository) {
            this.serviceStatusRepository = serviceStatusRepository;
        }

    @Transactional
    public void saveServiceStatus(ServiceStatus status) {
        serviceStatusRepository.save(status);
    }

        @Scheduled(fixedRate = 305000)
        public void fetchServiceStatus() {
            System.out.println("Fetching service status...");

            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");

            WebDriver driver = new ChromeDriver(options);

            try {
                driver.get(STATUS_PAGE_URL);
                Thread.sleep(3000);

                List<WebElement> serviceRows = driver.findElements(By.cssSelector("div.col-md-4.px-4 div.row.mr-2.mb-2"));

                for (WebElement row : serviceRows) {
                    String serviceName = row.findElement(By.tagName("td")).getText();

                    if (!TRACKED_SERVICES.contains(serviceName)) {
                        continue;
                    }

                    WebElement statusIcon = row.findElement(By.cssSelector("td span.fas"));
                    String extractedClass = statusIcon.getAttribute("class");

                    ServiceState newState = parseState(statusIcon);
                    ServiceState previousState = lastKnownStates.get(serviceName);

                    if (previousState == null || previousState != newState) {
                        ServiceStatus newStatus = new ServiceStatus(serviceName, newState);
                        System.out.println("Trying to save: " + serviceName + " -> " + newState);
                        saveServiceStatus(newStatus);
                        System.out.println("Saved: " + serviceName + " -> " + newState);
                    }

                    lastKnownStates.put(serviceName, newState);
                }

                System.out.println("Service status updated.");

            } catch (InterruptedException e) {
                System.err.println("Error waiting for JavaScript execution: " + e.getMessage());
            } finally {
                driver.quit();
            }
        }

        public List<ServiceStatus> getAllStatusChanges() {
            return serviceStatusRepository.findAll();
        }

        private ServiceState parseState(WebElement statusIcon) {
            if (statusIcon != null) {
                String statusClass = statusIcon.getAttribute("class");
                if (statusClass.contains("fa-check")) {
                    return ServiceState.SUCCESS;
                } else if (statusClass.contains("fa-exclamation")) {
                    return ServiceState.WARNING;
                } else if (statusClass.contains("fa-question")) {
                    return ServiceState.UNDEFINED;
                }
            }
            return ServiceState.UNDEFINED;
        }
    }
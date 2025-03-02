package example.com.idauptime;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class UptimeService {

    private static final String STATUS_PAGE_URL = "http://127.0.0.1:5000"; // Flask test server

    private final Map<String, ServiceState> lastKnownStates = new ConcurrentHashMap<>();
    private final List<ServiceStatus> statusHistory = new CopyOnWriteArrayList<>();

    // List of services to track
    private static final List<String> TRACKED_SERVICES = List.of(
            "Anmeldung im Web mit ID Austria",
            "Aktivierung der App \"Digitales Amt\"",
            "Anmeldung mit der App \"Digitales Amt\"",
            "Deaktivierung der App \"Digitales Amt\"",
            "eIDAS Knoten"
    );

    @Scheduled(fixedRate = 305000) // Runs every 5 minutes
    public void fetchServiceStatus() {
        System.out.println("Fetching service status...");

        // Setup ChromeDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(STATUS_PAGE_URL);
            Thread.sleep(3000); // Wait for JavaScript execution

            List<WebElement> serviceRows = driver.findElements(By.cssSelector("div.col-md-4.px-4 div.row.mr-2.mb-2"));

            for (WebElement row : serviceRows) {
                // Extract service name
                String serviceName = row.findElement(By.tagName("td")).getText();

                // Ignore services that are not in the tracked list
                if (!TRACKED_SERVICES.contains(serviceName)) {
                    continue;
                }

                // Extract the dynamically updated status icon
                WebElement statusIcon = row.findElement(By.cssSelector("td span.fas"));
                String extractedClass = statusIcon.getAttribute("class");

                // Log extracted service and status
                System.out.println("Service Name: " + serviceName);
                System.out.println("Extracted Icon Class: " + extractedClass);

                ServiceState newState = parseState(statusIcon);
                ServiceState previousState = lastKnownStates.get(serviceName);

                if (previousState == null || previousState != newState) {
                    statusHistory.add(new ServiceStatus(serviceName, newState));
                    System.out.println("State change detected: " + serviceName + " -> " + newState);
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
        return statusHistory;
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
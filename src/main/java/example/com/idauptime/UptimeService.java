package example.com.idauptime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UptimeService {

    private static final String STATUS_PAGE_URL = "http://127.0.0.1:5000/";

    // Concurrent HashMap ensures thread safety for scheduled tasks
    private final Map<String, ServiceStatus> serviceStatusMap = new ConcurrentHashMap<>();


    @Scheduled(cron = "*/5 * * * * *")
    // TODO make the schedule time configurable in the properties
    public void fetchServiceStatus() {
        try {
            System.out.println("Fetching service status...");
            Document doc = Jsoup.connect(STATUS_PAGE_URL).get();

            Element servicesContainer = doc.selectFirst("div.col-md-4.px-4");
            if (servicesContainer == null) {
                throw new IOException("Service container not found.");
            }

            Elements serviceRows = servicesContainer.select("div.row.mr-2.mb-2");

            for (Element row : serviceRows) {
                String serviceName = row.select("td").first().text();
                Element statusIcon = row.select("td span").first();
                String newStatus = parseStatus(statusIcon);

                ServiceStatus lastStatus = serviceStatusMap.getOrDefault(serviceName, new ServiceStatus(serviceName));

                // Handle status change (degradation or restoration)
                if (!lastStatus.getCurrentStatus().equals(newStatus)) {
                    if (lastStatus.getCurrentStatus().equals("Success") && !newStatus.equals("Success")) {
                        lastStatus.setTimestampDegradation(LocalDateTime.now());
                    } else if (!lastStatus.getCurrentStatus().equals("Success") && newStatus.equals("Success")) {
                        lastStatus.setTimestampRestoration(LocalDateTime.now());
                    }
                }

                // Update status in the map
                lastStatus.setCurrentStatus(newStatus);
                serviceStatusMap.put(serviceName, lastStatus);
            }

            System.out.println("Service status updated.");
        } catch (IOException e) {
            System.err.println("Error fetching status page: " + e.getMessage());
        }
    }

    public Map<String, ServiceStatus> getCurrentServiceStatus() {
        return serviceStatusMap;
    }

    private String parseStatus(Element statusIcon) {
        if (statusIcon != null) {
            String statusClass = statusIcon.className();
            if (statusClass.contains("fa-check")) {
                return "Success";
            } else if (statusClass.contains("fa-exclamation")) {
                return "Warning";
            } else if (statusClass.contains("fa-question")) {
                return "Undefined";
            }
        }
        return "Unknown";
    }
}
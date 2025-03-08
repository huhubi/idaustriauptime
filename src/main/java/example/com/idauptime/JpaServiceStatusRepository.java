package example.com.idauptime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Repository
public class JpaServiceStatusRepository {

    private static final Logger logger = Logger.getLogger(JpaServiceStatusRepository.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    public List<ServiceStatusDTO> findLatestStatusForAllServices() {
        logger.info("Fetching latest status and last incident timestamps for all services");

        // Query 1: Get the latest overall status per service
        String latestStatusQuery = """
                SELECT name, current_state, timestamp FROM (
                    SELECT name, current_state, timestamp FROM activate_app_service_status ORDER BY timestamp DESC LIMIT 1
                ) AS a
                UNION ALL
                SELECT name, current_state, timestamp FROM (
                    SELECT name, current_state, timestamp FROM deactivate_app_service_status ORDER BY timestamp DESC LIMIT 1
                ) AS b
                UNION ALL
                SELECT name, current_state, timestamp FROM (
                    SELECT name, current_state, timestamp FROM eidas_service_status ORDER BY timestamp DESC LIMIT 1
                ) AS c
                UNION ALL
                SELECT name, current_state, timestamp FROM (
                    SELECT name, current_state, timestamp FROM login_app_service_status ORDER BY timestamp DESC LIMIT 1
                ) AS d
                UNION ALL
                SELECT name, current_state, timestamp FROM (
                    SELECT name, current_state, timestamp FROM login_web_service_status ORDER BY timestamp DESC LIMIT 1
                ) AS e
                """;

        // Query 2: Get the latest NOT OK status (WARNING or UNDEFINED) with type
        String lastIncidentQuery = """
                SELECT name, current_state, timestamp
                FROM (
                         SELECT * FROM (
                                           SELECT name, current_state, timestamp
                                           FROM activate_app_service_status
                                           WHERE current_state IN ('WARNING', 'UNDEFINED')
                                           ORDER BY timestamp DESC
                                           LIMIT 1
                                       ) AS sub_1

                         UNION ALL

                         SELECT * FROM (
                                           SELECT name, current_state, timestamp
                                           FROM deactivate_app_service_status
                                           WHERE current_state IN ('WARNING', 'UNDEFINED')
                                           ORDER BY timestamp DESC
                                           LIMIT 1
                                       ) AS sub_2

                         UNION ALL

                         SELECT * FROM (
                                           SELECT name, current_state, timestamp
                                           FROM eidas_service_status
                                           WHERE current_state IN ('WARNING', 'UNDEFINED')
                                           ORDER BY timestamp DESC
                                           LIMIT 1
                                       ) AS sub_3

                         UNION ALL

                         SELECT * FROM (
                                           SELECT name, current_state, timestamp
                                           FROM login_app_service_status
                                           WHERE current_state IN ('WARNING', 'UNDEFINED')
                                           ORDER BY timestamp DESC
                                           LIMIT 1
                                       ) AS sub_4

                         UNION ALL

                         SELECT * FROM (
                                           SELECT name, current_state, timestamp
                                           FROM login_web_service_status
                                           WHERE current_state IN ('WARNING', 'UNDEFINED')
                                           ORDER BY timestamp DESC
                                           LIMIT 1
                                       ) AS sub_5
                     ) AS incidents;
                """;

        logger.info("Executing SQL Query for latest status:\n" + latestStatusQuery);
        List<Object[]> latestStatusResults = entityManager.createNativeQuery(latestStatusQuery).getResultList();

        logger.info("Executing SQL Query for last incidents:\n" + lastIncidentQuery);
        List<Object[]> lastIncidentResults = entityManager.createNativeQuery(lastIncidentQuery).getResultList();

        // Convert lastIncidentResults into a Map for fast lookup
        Map<String, IncidentInfo> lastIncidentMap = lastIncidentResults.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0], // Service name
                        row -> new IncidentInfo(
                                row[1] != null ? (String) row[1] : "No Incident", // Incident type (WARNING or UNDEFINED)
                                row[2] != null ? ((Timestamp) row[2]).toLocalDateTime() : null // Incident timestamp
                        )
                ));

        // Convert latest status query results to DTOs
        return latestStatusResults.stream()
                .map(row -> {
                    String serviceName = (String) row[0];
                    IncidentInfo incidentInfo = lastIncidentMap.getOrDefault(serviceName, new IncidentInfo("No Incident", null));
                    return new ServiceStatusDTO(
                            serviceName,                        // name
                            (String) row[1],                    // current_state
                            row[2] != null ? ((Timestamp) row[2]).toLocalDateTime() : null, // latest status timestamp
                            incidentInfo.timestamp,             // last incident timestamp
                            incidentInfo.state                  // last incident type (WARNING/UNDEFINED)
                    );
                })
                .toList();
    }

    // Helper class to store incident info
    private record IncidentInfo(String state, LocalDateTime timestamp) {}
}
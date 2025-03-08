package example.com.idauptime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class JpaServiceStatusRepository {

    private static final Logger logger = Logger.getLogger(JpaServiceStatusRepository.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    public List<ServiceStatusDTO> findLatestStatusForAllServices() {
        logger.info("Fetching latest status for all services");

        String sqlQuery = """
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

        logger.info("Executing SQL Query:\n" + sqlQuery);

        List<Object[]> results = entityManager.createNativeQuery(sqlQuery).getResultList();

        if (results.isEmpty()) {
            logger.warning("No status found for any table");
        } else {
            logger.info("Successfully retrieved " + results.size() + " status records.");
        }

        return results.stream()
                .map(row -> new ServiceStatusDTO(
                        (String) row[0],   // name
                        (String) row[1],   // current_state (enum as string)
                        row[2] != null ? ((Timestamp) row[2]).toLocalDateTime() : null // Convert Timestamp -> LocalDateTime
                ))
                .toList();
    }
}
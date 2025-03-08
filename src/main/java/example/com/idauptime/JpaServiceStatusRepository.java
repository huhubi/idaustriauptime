package example.com.idauptime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public class JpaServiceStatusRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ServiceStatus> findLatestStatusForService(String tableName) {
        return entityManager.createNativeQuery(
                        "SELECT id, name, current_state, timestamp FROM " + tableName + " ORDER BY timestamp DESC LIMIT 1", ServiceStatus.class)
                .getResultList();
    }

    public List<ServiceStatus> findHistoryForService(String serviceName) {
        String tableName = getTableName(serviceName);
        return entityManager.createNativeQuery(
                        "SELECT id, name, current_state, timestamp FROM " + tableName + " ORDER BY timestamp DESC", ServiceStatus.class)
                .getResultList();
    }

    private String getTableName(String serviceName) {
        return switch (serviceName) {
            case "Anmeldung im Web mit ID Austria" -> "login_web_service_status";
            case "Aktivierung der App \"Digitales Amt\"" -> "activate_app_service_status";
            case "Anmeldung mit der App \"Digitales Amt\"" -> "login_app_service_status";
            case "Deaktivierung der App \"Digitales Amt\"" -> "deactivate_app_service_status";
            case "eIDAS Knoten" -> "eidas_service_status";
            default -> throw new IllegalArgumentException("Unknown service: " + serviceName);
        };
    }
}
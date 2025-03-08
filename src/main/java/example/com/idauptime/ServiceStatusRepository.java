package example.com.idauptime;



import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ServiceStatusRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // Mapping between service names and corresponding tables
    private static final Map<String, String> SERVICE_TABLES = Map.of(
            "Anmeldung im Web mit ID Austria", "login_web_service_status",
            "Aktivierung der App \"Digitales Amt\"", "activate_app_service_status",
            "Anmeldung mit der App \"Digitales Amt\"", "login_app_service_status",
            "Deaktivierung der App \"Digitales Amt\"", "deactivate_app_service_status",
            "eIDAS Knoten", "eidas_service_status"
    );

    @Transactional
    public void save(ServiceStatus status) {
        String tableName = SERVICE_TABLES.get(status.getName());
        if (tableName == null) {
            throw new IllegalArgumentException("Unknown service name: " + status.getName());
        }

        entityManager.createNativeQuery(
                        "INSERT INTO " + tableName + " (name, current_state, timestamp) VALUES (:name, :state, :timestamp)")
                .setParameter("name", status.getName())
                .setParameter("state", status.getCurrentState().toString())
                .setParameter("timestamp", status.getTimestamp())
                .executeUpdate();
    }
}
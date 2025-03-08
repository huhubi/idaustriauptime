package example.com.idauptime;


import org.springframework.stereotype.Component;

@Component
public class DatabaseServiceStatusObserver implements ServiceStatusObserver {

    private final ServiceStatusRepository repository;

    public DatabaseServiceStatusObserver(ServiceStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public void update(String serviceName, ServiceState state) {
        ServiceStatus status = new ServiceStatus(serviceName, state);
        repository.save(status);
        System.out.println("Saved status update for " + serviceName + ": " + state);
    }
}

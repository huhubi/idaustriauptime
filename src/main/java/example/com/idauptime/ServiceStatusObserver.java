package example.com.idauptime;

public interface ServiceStatusObserver {
    void update(String serviceName, ServiceState state);
}

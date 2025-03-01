package example.com.idauptime;


public enum ServiceState {
    SUCCESS, WARNING, UNDEFINED;

    /**
     * Determines if a state transition should be logged.
     */
    public boolean shouldLogTransition(ServiceState previousState) {
        if (previousState == null) {
            return true; // First time a service is seen
        }
        return !this.equals(previousState); // Log only if the state changes
    }


}
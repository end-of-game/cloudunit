package fr.treeptik.cloudunit.domain.core;

public enum ApplicationState {
    STARTING(true),
    STARTED,
    STOPPING(true),
    STOPPED,
    REMOVING(true),
    FAILED;
    
    private boolean pending = false;
    
    public boolean isPending() {
        return pending;
    }
    
    ApplicationState() {}
    
    ApplicationState(boolean pending) {
        this.pending = pending;
    }
}

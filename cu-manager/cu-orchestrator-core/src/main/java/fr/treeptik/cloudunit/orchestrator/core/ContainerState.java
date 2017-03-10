package fr.treeptik.cloudunit.orchestrator.core;

public enum ContainerState {
    STARTING(true),
    STARTED(STARTING),
    STOPPING(true),
    STOPPED(STOPPING),
    REMOVING(true),
    FAILED;

    private final boolean pending; 
    
    private final ContainerState pendingState;

    ContainerState() {
        this.pending = false;
        this.pendingState = null;
    }

    ContainerState(ContainerState pendingState) {
        this.pending = false;
        this.pendingState = pendingState;
    }
    
    ContainerState(boolean pending) {
        this.pending = pending;
        this.pendingState = null;
    }

    public boolean isPending() {
        return pending;
    }

    public ContainerState getPendingState() {
        return pendingState;
    }
}

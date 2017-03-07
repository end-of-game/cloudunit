package fr.treeptik.cloudunit.orchestrator.core;

public enum ContainerState {
    STARTING,
    STARTED(STARTING),
    STOPPING,
    STOPPED(STOPPING),
    REMOVING,
    FAILED;

    private final ContainerState pendingState;

    ContainerState() {
        pendingState = null;
    }

    ContainerState(ContainerState pendingState) {
        this.pendingState = pendingState;
    }

    public ContainerState getPendingState() {
        return pendingState;
    }
}

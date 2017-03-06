package fr.treeptik.cloudunit.orchestrator.core;

public enum ContainerState {
    STARTING,
    STARTED,
    STOPPING,
    STOPPED,
    REMOVING,
    FAILED;
}

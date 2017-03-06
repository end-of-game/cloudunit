package fr.treeptik.cloudunit.orchestrator.core;

public interface ContainerEventListener {
    void onContainerStart(Container container);
    
    void onContainerStop(Container container);
    
    void onContainerRemove(Container containerName);
}

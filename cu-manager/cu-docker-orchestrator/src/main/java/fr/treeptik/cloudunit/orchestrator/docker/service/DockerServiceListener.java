package fr.treeptik.cloudunit.orchestrator.docker.service;

import fr.treeptik.cloudunit.orchestrator.core.Container;

public interface DockerServiceListener {
    void onContainerCreated(Container container);

    void onContainerChanged(Container container);

    void onContainerDeleted(Container container);
}

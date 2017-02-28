package fr.treeptik.cloudunit.orchestrator.docker.service;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;

public interface DockerService {
    Container createContainer(String name, Image image);

    void deleteContainer(Container container);
}

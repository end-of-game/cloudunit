package fr.treeptik.cloudunit.orchestrator.docker.service;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;

import java.util.Map;

public interface DockerService {
    Container createContainer(String name, Image image);

    Variable addVariable(Container container, String key, String value);

    void deleteContainer(Container container);

    void startContainer(Container container);

    void stopContainer(Container container);

}

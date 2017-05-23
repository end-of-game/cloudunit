package fr.treeptik.cloudunit.orchestrator.docker.service;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.Mount;
import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.core.Volume;
import fr.treeptik.cloudunit.orchestrator.docker.service.impl.DockerServiceImpl.ExecutionResult;

public interface DockerService {
    Container createContainer(String name, Image image);

    Variable addVariable(Container container, String key, String value);

    void deleteContainer(Container container);

    void startContainer(Container container);

    void stopContainer(Container container);

    Variable updateVariable(Container container, Variable variable, String value);

    void removeVariable(Container container, Variable variable);

    Volume createVolume(String name);

    void deleteVolume(Volume volume);

    Mount mountVolume(Container container, Volume volume, String mountPoint);
    
    void unmountVolume(Container container, Mount mount);

    void addDependency(Container container, Container dependency);

    void removeDependency(Container container, String dependency);

	void sendFileToContainer(String containerId, String localPathFile, String originalName, String filePath);

	ExecutionResult execute(Container container, String... cmd);
}

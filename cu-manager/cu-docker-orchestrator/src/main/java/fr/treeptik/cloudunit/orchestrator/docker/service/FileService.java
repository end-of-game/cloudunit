package fr.treeptik.cloudunit.orchestrator.docker.service;

import fr.treeptik.cloudunit.orchestrator.core.Container;

public interface FileService {

	void deploy(Container container, String fileUri, String contextPath);

	void sendFileToContainer(String containerId, String fileUri, String destination);

}

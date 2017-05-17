package fr.treeptik.cloudunit.orchestrator.docker.service;

import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.orchestrator.core.Container;

public interface FileService {

	void sendFileToContainer(Container container, String destination, MultipartFile fileUpload);

	void deploy(Container container, MultipartFile fileUpload);

}

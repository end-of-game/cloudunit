package fr.treeptik.cloudunit.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.domain.core.Image;

public interface OrchestratorService {
	List<Image> findAllImages();

	Optional<Image> findImageByName(String imageName);

	void deploy(String containerName, String contextPath, MultipartFile file);

	void undeploy(String containerName, String contextPath);

}

package fr.treeptik.cloudunit.domain.service;

import java.util.List;
import java.util.Optional;

import fr.treeptik.cloudunit.domain.core.Image;

public interface OrchestratorService {
	List<Image> findAllImages();

	Optional<Image> findImageByName(String imageName);

	String deploy(String containerName, String contextPath, String fileUri);

}

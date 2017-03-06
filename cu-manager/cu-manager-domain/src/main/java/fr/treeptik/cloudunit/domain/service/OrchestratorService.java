package fr.treeptik.cloudunit.domain.service;

import java.util.List;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Image;

public interface OrchestratorService {
    List<Image> findAllImages();

    void createContainer(Application application, String containerName, Image image);

    void deleteContainer(Application application, String containerName);

    void startContainer(String containerName);

    void stopContainer(String containerName);
}

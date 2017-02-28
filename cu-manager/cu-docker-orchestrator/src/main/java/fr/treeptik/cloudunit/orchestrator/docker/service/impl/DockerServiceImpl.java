package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.RemoveContainerParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.docker.service.ServiceException;

@Component
public class DockerServiceImpl implements DockerService {
    private static final String FILTER_LABEL_KEY = "origin";
    private static final String FILTER_LABEL_VALUE = "cloudunit";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerServiceImpl.class);
    
    @Autowired
    private ContainerRepository containerRepository;
    
    @Autowired
    private DockerClient docker;
    
    @PostConstruct
    public void logInfo() {
        try {
            LOGGER.info(docker.info().toString());
        } catch (DockerException | InterruptedException e) {
            LOGGER.warn("Could not get info about Docker");
        }
    }

    @Override
    public Container createContainer(String name, Image image) {
        Container container = new Container(name, image);
        
        MDC.put("container", container.toString());
        
        LOGGER.info("Creating container");
        
        try {
            doCreateContainer(container);
            
            container = containerRepository.save(container);
            
            return container;
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't create a container", e);
            throw new ServiceException("Couldn't create container", e);
        } finally {
            MDC.remove("container");
        }
    }
    
    private void doCreateContainer(Container container) throws DockerException, InterruptedException {
        Map<String, String> labels = new HashMap<>();
        labels.put(FILTER_LABEL_KEY, FILTER_LABEL_VALUE);
        
        ContainerConfig config = ContainerConfig.builder()
                .hostname(container.getName())
                .image(container.getImageName())
                .labels(labels)
                .build();
        
        ContainerCreation containerCreation = docker.createContainer(config, container.getName());

        if (containerCreation.warnings() != null) {
            containerCreation.warnings().forEach(warning -> {
                LOGGER.warn(warning);
            });
        }
    }

    @Override
    public void deleteContainer(Container container) {
        MDC.put("container", container.toString());
        
        LOGGER.info("Deleting container");
        try {
            docker.removeContainer(container.getName(), RemoveContainerParam.forceKill(true));
            
            containerRepository.delete(container);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't delete a container", e);
            throw new ServiceException("Couldn't delte a container", e);
        } finally {
            MDC.remove("container");
        }
    }
}

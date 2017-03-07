package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import fr.treeptik.cloudunit.orchestrator.core.*;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.EventsParam;
import com.spotify.docker.client.DockerClient.RemoveContainerParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.Event;
import com.spotify.docker.client.messages.Event.Type;

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

    @Autowired
    private ThreadFactory threadFactory;
    
    private int secondsBeforeKilling = 30;
    
    private List<ContainerEventListener> containerListeners = new LinkedList<>();
    
    public void setSecondsBeforeKilling(int secondsBeforeKilling) {
        this.secondsBeforeKilling = secondsBeforeKilling;
    }
    
    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }
    
    public void addContainerListener(ContainerEventListener listener) {
        containerListeners.add(listener);
    }
    
    public void removeContainerListener(ContainerEventListener listener) {
        containerListeners.remove(listener);
    }
    
    @PostConstruct
    public void start() {
        try {
            LOGGER.info(docker.info().toString());
            
            threadFactory.newThread(this::listenContainerEvents).start();
        } catch (DockerException | InterruptedException e) {
            LOGGER.warn("Could not get info about Docker");
        }
    }

    @Override
    public Variable addVariable(Container container, String key, String value) {
        MDC.put("container", container.toString());

        LOGGER.info("adding variable to container");

        try {
            container.setPending();
            containerRepository.save(container);
            doDeleteContainer(container);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't delete container", e);
        }
        Variable variable = container.addVariable(key, value);
        containerRepository.save(container);
        return variable;
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
                .env(container.getVariablesAsList())
                .build();
        
        ContainerCreation containerCreation = docker.createContainer(config, container.getName());

        if (containerCreation.warnings() != null) {
            containerCreation.warnings().forEach(warning -> {
                LOGGER.warn(warning);
            });
        }
        
        container.setContainerId(containerCreation.id());
    }

    @Override
    public void deleteContainer(Container container) {
        MDC.put("container", container.toString());
        
        LOGGER.info("Deleting container");
        try {
            container.setState(ContainerState.REMOVING);
            containerRepository.save(container);
            doDeleteContainer(container);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't delete a container", e);
            throw new ServiceException("Couldn't delete a container", e);
        } finally {
            MDC.remove("container");
        }
    }


    @Override
    public void startContainer(Container container) {
        MDC.put("container", container.toString());
        try {
            container.setState(ContainerState.STARTING);
            containerRepository.save(container);
            doStartContainer(container);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't start a container", e);
        } finally {
            MDC.remove("container");
        }
    }

    @Override
    public void stopContainer(Container container) {
        MDC.put("container", container.toString());
        try {
            container.setState(ContainerState.STOPPING);
            containerRepository.save(container);

            docker.stopContainer(container.getName(), secondsBeforeKilling);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't start a container", e);
        } finally {
            MDC.remove("container");
        }
    }
    
    private void listenContainerEvents() {
        try {
            for (Iterator<Event> events = docker.events(EventsParam.type(Type.CONTAINER)); events.hasNext();) {
                Event event = events.next();
                
                LOGGER.debug("Received event {}", event);
                
                String name = event.actor().attributes().get("name");
                containerRepository.findByName(name).ifPresent(container -> {
                    switch (event.action()) {
                    case "create":
                        onContainerCreate(container);
                    case "start":
                        onContainerStart(container);
                        break;
                    case "die":
                        onContainerStop(container);
                        break;
                    case "destroy":
                        onContainerRemove(container);
                        break;
                    default:
                        break;
                    }                    
                });
                
            }
            
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't listen to Docker events");
        }
    }

    private void doStartContainer(Container container) throws DockerException, InterruptedException {
        docker.startContainer(container.getName());
    }

    private void doDeleteContainer(Container container) throws DockerException, InterruptedException {
        docker.removeContainer(container.getName(), RemoveContainerParam.forceKill(true));
    }

    private void onContainerCreate(Container container) {
        LOGGER.info("Container {} created", container.getName());
        if (container.getState() == ContainerState.STOPPING) {
            container.setState(ContainerState.STOPPED);
        } else if (container.getState() == ContainerState.STARTING) {
            try {
                doStartContainer(container);
            } catch (DockerException | InterruptedException e) {
                LOGGER.error("Couldn't start container", e);
            }
        }
        containerRepository.save(container);
        containerListeners.forEach(listener -> listener.onContainerStart(container));
    }

    private void onContainerStart(Container container) {
        LOGGER.info("Container {} started", container.getName());
        container.setState(ContainerState.STARTED);
        containerRepository.save(container);
        containerListeners.forEach(listener -> listener.onContainerStart(container));
    }

    private void onContainerStop(Container container) {
        LOGGER.info("Container {} stopped", container.getName());
        container.setState(ContainerState.STOPPED);
        containerRepository.save(container);
        containerListeners.forEach(listener -> listener.onContainerStop(container));
    }

    private void onContainerRemove(Container container) {
        LOGGER.info("Container {} removed", container.getName());
        if (container.getState() == ContainerState.REMOVING) {
            containerRepository.delete(container);
            containerListeners.forEach(listener -> listener.onContainerRemove(container));
        } else {
            try {
                doCreateContainer(container);
            } catch (DockerException | InterruptedException e) {
                LOGGER.error("Couldn't create container", e);
            }
        }
    }

}

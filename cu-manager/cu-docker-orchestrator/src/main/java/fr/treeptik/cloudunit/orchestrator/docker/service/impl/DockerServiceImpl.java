package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.EventsParam;
import com.spotify.docker.client.DockerClient.ExecCreateParam;
import com.spotify.docker.client.DockerClient.RemoveContainerParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerConfig.NetworkingConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.EndpointConfig;
import com.spotify.docker.client.messages.Event;
import com.spotify.docker.client.messages.Event.Type;
import com.spotify.docker.client.messages.ExecCreation;
import com.spotify.docker.client.messages.ExecState;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.HostConfig.Bind;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.Mount;
import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.core.Volume;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.repository.VolumeRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerServiceListener;
import fr.treeptik.cloudunit.orchestrator.docker.service.ServiceException;

@Component
public class DockerServiceImpl implements DockerService {
    private static final String CONTAINER_MDC = "container";
    private static final String FILTER_LABEL_KEY = "io.cloudunit";
    private static final String FILTER_LABEL_VALUE = "";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerServiceImpl.class);
    private static final String DEFAULT_NETWORK = "skynet";
    
    @Autowired
    private ContainerRepository containerRepository;
    
    @Autowired
    private VolumeRepository volumeRepository;

    @Autowired
    private DockerClient docker;
    
    @Autowired
    private List<DockerServiceListener> listeners;

    @Autowired
    private ThreadFactory threadFactory;
    
    public DockerServiceImpl() {
        listeners = new ArrayList<>();
    }
    
    private int secondsBeforeKilling = 30;
    
    public void setSecondsBeforeKilling(int secondsBeforeKilling) {
        this.secondsBeforeKilling = secondsBeforeKilling;
    }
    
    public void setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
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
    public Container createContainer(String name, Image image) {
        Container container = new Container(name, image);
        
        MDC.put(CONTAINER_MDC, container.toString());
        try {
            LOGGER.info("Creating container {}", name);
            container = containerRepository.save(container);
            fireContainerCreated(container);
    
            doCreateContainer(container);
            return container;
        } finally {
            MDC.remove(CONTAINER_MDC);
        }
    }
    
    @Override
    public void deleteContainer(Container container) {
        MDC.put(CONTAINER_MDC, container.toString());
        
        LOGGER.info("Deleting container");
        try {
            container.setState(ContainerState.REMOVING);
            container = containerRepository.save(container);

            doDeleteContainer(container);
        } finally {
            MDC.remove(CONTAINER_MDC);
        }
    }

    @Override
    public void startContainer(Container container) {
        MDC.put(CONTAINER_MDC, container.toString());
        try {
            container.setState(ContainerState.STARTING);
            container = containerRepository.save(container);

            doStartContainer(container);
        } finally {
            MDC.remove(CONTAINER_MDC);
        }
    }

    @Override
    public void stopContainer(Container container) {
        MDC.put(CONTAINER_MDC, container.toString());
        try {
            container.setState(ContainerState.STOPPING);
            container = containerRepository.save(container);

            doStopContainer(container);
        } finally {
            MDC.remove(CONTAINER_MDC);
        }
    }

    @Override
    public Variable addVariable(Container container, String key, String value) {
        MDC.put(CONTAINER_MDC, container.toString());

        LOGGER.info("Adding variable {}={} to container {}", key, value, container.getName());
        try {
            Variable variable = container.addVariable(key, value);
            container.setPending();
            container = containerRepository.save(container);
            
            doDeleteContainer(container);
            return variable;
        } finally {
            MDC.remove(CONTAINER_MDC);
        }
    }
    
    @Override
    public Variable updateVariable(Container container, Variable variable, String value) {
        MDC.put(CONTAINER_MDC, container.toString());
        try {
            LOGGER.debug("Updating variable {}={} of container {}", variable.getKey(), value, container.getName());
            variable.setValue(value);
            container.setPending();
            container = containerRepository.save(container);
            
            doDeleteContainer(container);
            return variable;
        } finally {
            MDC.remove(CONTAINER_MDC);
        }
    }
    
    @Override
    public void removeVariable(Container container, Variable variable) {
        MDC.put(CONTAINER_MDC, container.toString());
        try {
            container.removeVariable(variable);
            container.setPending();
            container = containerRepository.save(container);
            
            doDeleteContainer(container);
        } finally {
            MDC.remove(CONTAINER_MDC);
        }        
    }
    
    @Override
    public Volume createVolume(String name) {
        try {
            Map<String, String> labels = new HashMap<>();
            labels.put(FILTER_LABEL_KEY, FILTER_LABEL_VALUE);
            docker.createVolume(com.spotify.docker.client.messages.Volume.builder()
                    .name(name)
                    .labels(labels)
                    .build());
            Volume volume = new Volume(name);
            
            volume = volumeRepository.save(volume);
            return volume;
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't create volume", e);
            throw new ServiceException("Couldn't create volume", e);
        }
    }
    
    @Override
    public void deleteVolume(Volume volume) {
        try {
            docker.removeVolume(volume.getName());
            volumeRepository.delete(volume);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't remove volume", e);
            throw new ServiceException("Couldn't remove volume", e);
        }
    }

    @Override
    public Mount mountVolume(Container container, Volume volume, String mountPoint) {
        Mount mount = container.addMount(volume, mountPoint);
        container.setPending();
        container = containerRepository.save(container);
        doDeleteContainer(container);
        
        return mount;
    }
    
    @Override
    public void unmountVolume(Container container, Mount mount) {
        container.removeMount(mount.getVolumeName());
        container.setPending();
        container = containerRepository.save(container);
        doDeleteContainer(container);
    }
    
    @Override
    public void addDependency(Container container, Container dependency) {
        container.addDependency(dependency);
        container.setPending();
        container = containerRepository.save(container);
        doDeleteContainer(container);
    }
    
    @Override
    public void removeDependency(Container container, String dependency) {
        container.removeDependency(dependency);
        container.setPending();
        container = containerRepository.save(container);
        doDeleteContainer(container);
    }
    
    @Override
    public void sendFileToContainer(String containerId, String localPathFile, String originalName, String filePath) {
        try {
            Path path = Paths.get(localPathFile);
            docker.copyToContainer(path, containerId, filePath);
        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("containerId=").append(containerId);
            msgError.append("localPathFile=").append(localPathFile);
            throw new ServiceException(msgError.toString(), e);
        }
    }

    private ExecutionResult execute(Container container, String... cmd) {
        try {
            ExecCreation exec = docker.execCreate(container.getName(), cmd,
                    ExecCreateParam.attachStdout(),
                    ExecCreateParam.attachStderr());
            
            if (exec.warnings() != null) {
                exec.warnings().stream().forEach(warning -> {
                    LOGGER.warn(warning);
                });
            }
            
            String output;
            try (LogStream stream = docker.execStart(exec.id())) {
                output = stream.readFully();
            }
            
            ExecState state = docker.execInspect(exec.id());
            return new ExecutionResult(state.exitCode(), output);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't execute command", e);
            throw new ServiceException("Couldn't execute command", e);
        }
    }

    private void doCreateContainer(Container container) {
        HostConfig hostConfig = HostConfig.builder()
                .appendBinds(container.getMounts().stream()
                        .map(mount -> Bind
                                .from(mount.getVolumeName())
                                .to(mount.getMountPoint())
                                .build())
                        .toArray(Bind[]::new))
                .build();
        
        Map<String, EndpointConfig> endpointsConfig = new HashMap<>();
        endpointsConfig.put(DEFAULT_NETWORK, EndpointConfig.builder()
                .aliases(ImmutableList.of(container.getLocalDnsName()))
                .build());
        
        ContainerConfig config = ContainerConfig.builder()
                .hostname(container.getName())
                .image(container.getImageName())
                .env(container.getVariablesAsList())
                .hostConfig(hostConfig)
                .networkingConfig(NetworkingConfig.create(endpointsConfig))
                .build();
        
        try {
            ContainerCreation containerCreation = docker.createContainer(config, container.getName());
            if (containerCreation.warnings() != null) {
                containerCreation.warnings().forEach(warning -> {
                    LOGGER.warn(warning);
                });
            }
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't create container", e);
            throw new ServiceException("Couldn't create container", e);            
        }
        
        updateDependants(container);
    }
    
    private void updateDependants(Container container) {
        containerRepository.findByDependencies(container.getName()).forEach(dependant -> {
            dependant.importVariables(container);
            dependant.setPending();
            dependant = containerRepository.save(dependant);
            doDeleteContainer(dependant);
        });
    }

    private void doStartContainer(Container container) {
        try {
            docker.startContainer(container.getName());
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't start a container", e);
            throw new ServiceException("Couldn't start a container", e);
        }
    }

    private void doStopContainer(Container container) {
        try {
            docker.stopContainer(container.getName(), secondsBeforeKilling);
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't stop a container", e);
            throw new ServiceException("Couldn't stop a container", e);
        }
    }

    private void doDeleteContainer(Container container) {
        try {
            docker.removeContainer(container.getName(),
                    RemoveContainerParam.forceKill(),
                    RemoveContainerParam.removeVolumes());
        } catch (DockerException | InterruptedException e) {
            LOGGER.error("Couldn't delete a container", e);
            throw new ServiceException("Couldn't delete a container", e);
        }
    }

    private void listenContainerEvents() {
        try {
            for (Iterator<Event> events = docker.events(EventsParam.type(Type.CONTAINER)); events.hasNext();) {
                Event event = events.next();
                
                LOGGER.debug("Received event {}", event);
                
                String name = event.actor().attributes().get("name");
                
                containerRepository.findByName(name).ifPresent(container -> {
                    LOGGER.debug("Event for container {}", container);
                    switch (event.action()) {
                    case "create":
                        onContainerCreate(container);
                        break;
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

    private void onContainerCreate(Container container) {
        LOGGER.info("Container {} created", container.getName());
        if (container.getState() == ContainerState.STOPPING) {
            container.setState(ContainerState.STOPPED);
            container = containerRepository.save(container);
            fireContainerChanged(container);
            LOGGER.debug("Container {} is up and {}", container.getName(), container.getState());
        } else if (container.getState() == ContainerState.STARTING) {
            doStartContainer(container);
            container = containerRepository.save(container);
            fireContainerChanged(container);
        }
    }

    private void onContainerStart(Container container) {
        LOGGER.info("Container {} started", container.getName());
        if (container.getState() == ContainerState.STARTING) {
            container.setState(ContainerState.STARTED);
            container = containerRepository.save(container);
        }
        fireContainerChanged(container);
    }

    private void onContainerStop(Container container) {
        LOGGER.info("Container {} stopped", container.getName());
        if (container.getState() == ContainerState.STOPPING) {
            container.setState(ContainerState.STOPPED);
            container = containerRepository.save(container);
        }
        fireContainerChanged(container);
    }

    private void onContainerRemove(Container container) {
        LOGGER.info("Container {} removed", container.getName());
        if (container.getState() == ContainerState.REMOVING) {
            containerRepository.delete(container);
            fireContainerDeleted(container);
        } else {
            doCreateContainer(container);
        }
    }
    
    private void fireContainerCreated(Container container) {
        listeners.forEach(listener -> listener.onContainerCreated(container));
    }

    private void fireContainerChanged(Container container) {
        listeners.forEach(listener -> listener.onContainerChanged(container));
    }

    private void fireContainerDeleted(Container container) {
        listeners.forEach(listener -> listener.onContainerDeleted(container));
    }

    private static class ExecutionResult {
        public final int exitCode;
        
        @SuppressWarnings("unused")
        public final String output;
        
        public ExecutionResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
        
        public boolean isOK() {
            return exitCode == 0;
        }
    }
}

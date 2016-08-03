package fr.treeptik.cloudunit.service.impl;


import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import fr.treeptik.cloudunit.docker.core.DockerCloudUnitClient;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.utils.ContainerMapper;
import fr.treeptik.cloudunit.utils.ContainerUtils;
import fr.treeptik.cloudunit.utils.JvmOptionsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by guillaume on 01/08/16.
 */
@Service
public class DockerServiceImpl implements DockerService {

    private Logger logger = LoggerFactory.getLogger(DockerService.class);

    @Inject
    private ContainerMapper containerMapper;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${env.exec}")
    private String envExec;

    @Value("${database.hostname}")
    private String databaseHostname;

    @Value("${suffix.cloudunit.io}")
    private String suffixCloudUnitIO;

    @Inject
    private DockerClient dockerClient;

    @Inject
    private DockerCloudUnitClient dockerCloudUnitClient;

    @Override
    public void createServer(String name, Server server, String imagePath, User user) throws DockerJSONException {
        String sharedDir = JvmOptionsUtils.extractDirectory(server.getJvmOptions());
        List<String> volumes = Arrays.asList("java");
        if (sharedDir != null) {
            sharedDir = sharedDir + ":/cloudunit/shared:rw";
            volumes.add(sharedDir);
        }
        DockerContainer container = ContainerUtils.newCreateInstance(name, imagePath, volumes, null);
        dockerCloudUnitClient.createContainer(container);
    }

    @Override
    public Server startServer(String containerName, Server server) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerCloudUnitClient.startContainer(container);
        container = dockerCloudUnitClient.findContainer(container);
        server = containerMapper.mapDockerContainerToServer(container, server);
        return server;

    }

    @Override
    public void stopServer(String containerName) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerCloudUnitClient.stopContainer(container);
    }

    @Override
    public void killServer(String containerName) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerCloudUnitClient.killContainer(container);
    }

    @Override
    public void removeServer(String containerName) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerCloudUnitClient.removeContainer(container);
    }

    /**
     * Execute a shell conmmad into a container. Return the output as String
     *
     * @param containerName
     * @param command
     * @return
     */
    @Override
    public String execCommand(String containerName, String[] command)
            throws FatalDockerJSONException {
        try {
            String execId = dockerClient.execCreate(containerName, command,
                    com.spotify.docker.client.DockerClient.ExecCreateParam.attachStdout(),
                    com.spotify.docker.client.DockerClient.ExecCreateParam.attachStderr());
            try (final LogStream stream = dockerClient.execStart(execId)) {
                final String output = stream.readFully();
                return output;
            }
        } catch (DockerException | InterruptedException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("containerName:[").append(containerName).append("]");
            msgError.append(", command:[").append(command).append("]");
            throw new FatalDockerJSONException(msgError.toString(), e);
        }
    }

    /**
     * Execute a shell conmmad into a container. Return the output as String
     *
     * @param containerName
     * @param command
     * @return
     */
    @Override
    public String execCommand(String containerName, String command) throws FatalDockerJSONException {
        final String[] commands = {"bash", "-c", command};
        return execCommand(containerName, commands);
    }

    @Override
    public Boolean isRunning(String containerName) throws FatalDockerJSONException {
        try {
            final ContainerInfo info = dockerClient.inspectContainer("containerID");
            return info.state().running();
        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("containerName=").append(containerName);
            throw new FatalDockerJSONException(msgError.toString(), e);
        } finally {
            if (dockerClient != null) {
                dockerClient.close();
            }
        }
    }

    @Override
    public List<String> listContainers() throws FatalDockerJSONException {
        List<String> containersId = null;
        try {
            List<Container> containers = dockerClient.listContainers(DockerClient.ListContainersParam.allContainers());
            containersId = containers.stream().map(c -> c.id()).collect(Collectors.toList());
        } catch (DockerException | InterruptedException e) {
            logger.error(e.getMessage());
        }
        return containersId;
    }

    @Override
    public String getContainerId(String containerName) throws FatalDockerJSONException {
        try {
            final ContainerInfo info = dockerClient.inspectContainer(containerName);
            return info.id();
        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("containerName=").append(containerName);
            throw new FatalDockerJSONException(msgError.toString(), e);
        } finally {
            if (dockerClient != null) {
                dockerClient.close();
            }
        }
    }

    @Override
    public String getContainerNameFromId(String id) throws FatalDockerJSONException {
        try {
            final ContainerInfo info = dockerClient.inspectContainer(id);
            return info.name();
        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("id=").append(id);
            throw new FatalDockerJSONException(msgError.toString(), e);
        } finally {
            if (dockerClient != null) {
                dockerClient.close();
            }
        }
    }

}

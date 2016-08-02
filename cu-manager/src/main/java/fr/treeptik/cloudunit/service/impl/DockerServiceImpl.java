package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.docker.core.DockerClient;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.exception.DockerJSONException;
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


    @Override
    public void createServer(String name, Server server, String imagePath, User user) throws DockerJSONException {
        List<String> args = Arrays.asList(user.getLogin(), user.getPassword(), server
                    .getApplication().getRestHost(), server
                    .getApplication().getName(), server.getServerAction().getDefaultJavaRelease(),
                databasePassword, envExec, databaseHostname);
        String sharedDir = JvmOptionsUtils.extractDirectory(server.getJvmOptions());
        List<String> volumes = Arrays.asList("java");
        if (sharedDir != null) {
            sharedDir = sharedDir + ":/cloudunit/shared:rw";
            volumes.add(sharedDir);
        }
        DockerContainer container = ContainerUtils.newCreateInstance(name, imagePath, volumes, args);

        dockerClient.createContainer(container);

    }

    @Override
    public Server startServer(String containerName, Server server) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerClient.startContainer(container);
       container = dockerClient.findContainer(container);
       server = containerMapper.mapDockerContainerToServer(container, server);
       return server;

    }
    @Override
    public void stopServer(String containerName) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerClient.stopContainer(container);
    }

    @Override
    public void killServer(String containerName) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerClient.killContainer(container);
    }

    @Override
    public void removeServer(String containerName) throws DockerJSONException {
        DockerContainer container = ContainerUtils.newStartInstance(containerName,
                null, null, null);
        dockerClient.removeContainer(container);
    }


}

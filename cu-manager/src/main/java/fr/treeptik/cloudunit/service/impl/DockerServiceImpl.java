package fr.treeptik.cloudunit.service.impl;

import com.spotify.docker.client.*;
import com.spotify.docker.client.messages.*;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.DockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.nio.file.Paths;

/**
 * This class is a wrapper to docker spotify api to purpose main functions for CloudUnit Business
 */
@Service
public class DockerServiceImpl implements DockerService {

    private final Logger logger = LoggerFactory.getLogger(DockerServiceImpl.class);

    @Value("${docker.manager.ip:192.168.50.4:2376}")
    private String dockerManagerIp;

    @Value("${certs.dir.path}")
    private String certsDirPath;

    @Value("${docker.endpoint.mode}")
    private String dockerEndpointMode;

    private boolean isHttpMode;

    @PostConstruct
    public void initDockerEndPointMode() {
        if ("http".equalsIgnoreCase(dockerEndpointMode)) {
            logger.warn("Docker TLS mode is disabled");
            isHttpMode = true;
        } else {
            isHttpMode = false;
        }
    }

    @Override
    public Boolean isRunning(String containerName) throws CheckException, ServiceException {
        DockerClient dockerClient = getDockerClient();
        try {
            final ContainerInfo info = dockerClient.inspectContainer("containerID");
            return info.state().running();
        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("containerName=").append(containerName);
            throw new ServiceException(msgError.toString(), e);
        } finally {
            if (dockerClient != null) { dockerClient.close(); }
        }
    }

    @Override
    public String getContainerId(String containerName) throws CheckException, ServiceException {
        DockerClient dockerClient = getDockerClient();
        try {
            final ContainerInfo info = dockerClient.inspectContainer(containerName);
            return info.id();
        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("containerName=").append(containerName);
            throw new ServiceException(msgError.toString(), e);
        } finally {
            if (dockerClient != null) { dockerClient.close(); }
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
    public String exec(String containerName, String command)
            throws CheckException, ServiceException {
        DockerClient dockerClient = null;
        String execOutput = null;
        try {
            dockerClient = getDockerClient();
            final String[] commands = {"bash", "-c", command};
            String execId = dockerClient.execCreate(containerName, commands,
                    DockerClient.ExecParameter.STDOUT,
                    DockerClient.ExecParameter.STDERR);
            final LogStream output = dockerClient.execStart(execId);
            execOutput = output.readFully();
            if (output != null) {
                output.close();
            }
        } catch (InterruptedException | DockerException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("containerName:[").append(containerName).append("]");
            msgError.append(", command:[").append(command).append("]");
            logger.error(msgError.toString(), e);
        } finally {
            if (dockerClient != null) { dockerClient.close(); }
        }
        return execOutput;
    }

    /**
     * Return an instance of Spotify DockerClient
     *
     * @return
     */
    private DockerClient getDockerClient() {
        DockerClient dockerClient = null;
        try {
            if (isHttpMode) {
                dockerClient = DefaultDockerClient
                        .builder()
                        .uri("http://" + dockerManagerIp).build();
            } else {
                final DockerCertificates certs = new DockerCertificates(Paths.get(certsDirPath));
                dockerClient = DefaultDockerClient
                        .builder()
                        .uri("https://" + dockerManagerIp).dockerCertificates(certs).build();
            }
        } catch (Exception e) {
            logger.error("cannot instance docker client : ", e);
        }
        return dockerClient;
    }
}
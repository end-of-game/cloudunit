package fr.treeptik.cloudunit.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;

import fr.treeptik.cloudunit.docker.core.DockerCloudUnitClient;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.utils.ContainerMapper;
import fr.treeptik.cloudunit.utils.ContainerUtils;
import fr.treeptik.cloudunit.utils.FilesUtils;
import fr.treeptik.cloudunit.utils.JvmOptionsUtils;

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
	public void createServer(String containerName, Server server, String imagePath, User user, List<String> envs,
			boolean createMainVolume, List<String> volumes) throws DockerJSONException {
		String sharedDir = JvmOptionsUtils.extractDirectory(server.getJvmOptions());
		List<String> volumesFrom = Arrays.asList("java");
		if (volumes == null) {
			volumes = new ArrayList<>();
		}
		if (sharedDir != null) {
			volumes.add(sharedDir + ":/opt/cloudunit/shared:rw");
		}
		if (createMainVolume) {
			dockerCloudUnitClient.createVolume(containerName, "runtime");
		}
		// always mount the associated volume
		volumes.add(containerName + ":/opt/cloudunit:rw");
		logger.info("Volumes to add : " + volumes.toString());
		DockerContainer container = ContainerUtils.newCreateInstance(containerName, imagePath, volumesFrom, null,
				volumes, envs, false);
		dockerCloudUnitClient.createContainer(container);
	}

	@Override
	public Server startServer(String containerName, Server server) throws DockerJSONException {
		DockerContainer container = ContainerUtils.newStartInstance(containerName, null, null, null, false);
		dockerCloudUnitClient.startContainer(container);
		container = dockerCloudUnitClient.findContainer(container);
		server = containerMapper.mapDockerContainerToServer(container, server);
		return server;
	}

	@Override
	public void stopContainer(String containerName) throws DockerJSONException {
		DockerContainer container = ContainerUtils.newStartInstance(containerName, null, null, null, false);
		dockerCloudUnitClient.stopContainer(container);
	}

	@Override
	public void killServer(String containerName) throws DockerJSONException {
		DockerContainer container = ContainerUtils.newStartInstance(containerName, null, null, null, false);
		dockerCloudUnitClient.killContainer(container);
	}

	@Override
	public void removeContainer(String containerName, boolean removeVolume) throws DockerJSONException {
		DockerContainer container = ContainerUtils.newStartInstance(containerName, null, null, null, false);
		dockerCloudUnitClient.removeContainer(container);
		if (removeVolume) {
			dockerCloudUnitClient.removeVolume(containerName);
		}
	}

	@Override
	public String execCommand(String containerName, String command, boolean privileged)
			throws FatalDockerJSONException {
		final String[] commands = { "bash", "-c", command };
		String execId = null;
		try {
			if (privileged) {
				execId = dockerClient.execCreate(containerName, commands,
						com.spotify.docker.client.DockerClient.ExecCreateParam.attachStdout(),
						com.spotify.docker.client.DockerClient.ExecCreateParam.attachStderr(),
						com.spotify.docker.client.DockerClient.ExecCreateParam.user("root"));
			} else {
				execId = dockerClient.execCreate(containerName, commands,
						com.spotify.docker.client.DockerClient.ExecCreateParam.attachStdout(),
						com.spotify.docker.client.DockerClient.ExecCreateParam.attachStderr());
			}
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
		String output = execCommand(containerName, command, false);
		if (output.contains("Permission denied")) {
			logger.warn("[" + containerName + "] exec command in privileged mode : " + command);
			output = execCommand(containerName, command, true);
		}
		return output;
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
		}
	}

	@Override
	public Boolean isStoppedGracefully(String containerName) throws FatalDockerJSONException {
		try {
			final ContainerInfo info = dockerClient.inspectContainer(containerName);
			boolean exited = info.state().status().equalsIgnoreCase("Exited");
			if (info.state().exitCode() != 0) {
				logger.warn("The container may be brutally stopped. Its exit code is : " + info.state().exitCode());
			}
			return exited;
		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append("containerName=").append(containerName);
			throw new FatalDockerJSONException(msgError.toString(), e);
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
	@Cacheable(value = "monitoring", key = "#containerName")
	public String getContainerId(String containerName) throws FatalDockerJSONException {
		try {
			final ContainerInfo info = dockerClient.inspectContainer(containerName);
			return info.id();
		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append("containerName=").append(containerName);
			throw new FatalDockerJSONException(msgError.toString(), e);
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
		}
	}

	@Override
	@Cacheable(value = "env", key = "{#containerName,#variable}")
	public String getEnv(String containerName, String variable) throws FatalDockerJSONException {
		try {
			Optional<String> value = dockerClient.inspectContainer(containerName).config().env().stream()
					.filter(e -> e.startsWith(variable)).map(s -> s.substring(s.indexOf("=") + 1)).findFirst();
			logger.info("VARIABLE=" + value);
			return (value.orElseThrow(() -> new ServiceException(variable + " is missing into DOCKERFILE.")));
		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append("containerId=").append(containerName);
			msgError.append("variable=").append(variable);
			throw new FatalDockerJSONException(msgError.toString(), e);
		}
	}

	@Override
	public void addEnv(String containerId, String key, String value) throws FatalDockerJSONException {
		try {
			Map<String, String> kvStore = new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					put("CU_KEY", key);
					put("CU_VALUE", value);
				}
			};
			execCommand(containerId, RemoteExecAction.ADD_ENV.getCommand(kvStore), true);
		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append("containerId=").append(containerId);
			msgError.append(",key=").append(key);
			msgError.append(",value=").append(value);
			throw new FatalDockerJSONException(msgError.toString(), e);
		}
	}

	@Override
	public int getFileFromContainer(String containerId, String path, OutputStream outputStream)
			throws FatalDockerJSONException {
		try {
			InputStream inputStream = dockerClient.archiveContainer(containerId, path);
			FilesUtils.unTar(inputStream, outputStream);
			int size = inputStream.available();
			return size;
		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append("containerId=").append(containerId);
			msgError.append("path=").append(path);
			throw new FatalDockerJSONException(msgError.toString(), e);
		}
	}

	@Override
	public void sendFileToContainer(String containerId, String localPathFile, String originalName, String filePath)
			throws FatalDockerJSONException {
		try {
			Path path = Paths.get(localPathFile);
			dockerClient.copyToContainer(path, containerId, filePath);
		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append("containerId=").append(containerId);
			msgError.append("localPathFile=").append(localPathFile);
			throw new FatalDockerJSONException(msgError.toString(), e);
		}
	}

	@Override
	public void createModule(String containerName, Module module, String imagePath, User user, List<String> envs,
			boolean createMainVolume, List<String> volumes) throws DockerJSONException {
		if (createMainVolume) {
			dockerCloudUnitClient.createVolume(containerName, "runtime");
		}
		volumes.add(containerName + ":/opt/cloudunit:rw");
		logger.info("Volumes to add : " + volumes.toString());
		DockerContainer container = ContainerUtils.newCreateInstance(containerName, imagePath, null, null, volumes,
				envs, module.getPublishPorts());
		dockerCloudUnitClient.createContainer(container);
	}

	@Override
	public Module startModule(String containerName, Module module) throws DockerJSONException {
		DockerContainer container = ContainerUtils.newStartInstance(containerName, null, null, null,
				module.getPublishPorts());
		dockerCloudUnitClient.startContainer(container);
		container = dockerCloudUnitClient.findContainer(container);
		module = containerMapper.mapDockerContainerToModule(container, module);
		return module;
	}

}

package fr.treeptik.cloudunit.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStopEvent;
import fr.treeptik.cloudunit.dao.EnvironmentDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.service.VolumeService;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

	@Inject
	private EnvironmentDAO environmentDAO;

	@Inject
	private ApplicationService applicationService;

	@Inject
	private ApplicationEventPublisher applicationEventPublisher;

	@Inject
	private VolumeService volumeService;

	@Inject
	private DockerService dockerService;

	@Inject
	private ServerService serverService;

	@Override
	@Transactional
	public EnvironmentVariable save(User user, EnvironmentVariable environment, String applicationName,
			String containerName) {
		checkEnvironmentVariableConsistence(environment, containerName);
		Application application = null;
		Server server = null;
		try {
			server = serverService.findByName(containerName);
			application = applicationService.findByNameAndUser(user, applicationName);
			stopAndRemoveServer(server, application);
			environment.setApplication(application);
			environment.setContainerName(containerName);
			environment = environmentDAO.save(environment);
			recreateAndMountVolumes(server, application);
		} catch (CheckException e) {
			throw new CheckException(e.getMessage());
		} catch (ServiceException e) {
			throw new CheckException(e.getMessage());
		} finally {
			applicationEventPublisher.publishEvent(new ServerStartEvent(server));
			applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
		}

		return environment;
	}

	@Override
	public EnvironmentVariable loadEnvironnment(int id) throws ServiceException, CheckException {
		EnvironmentVariable environment = environmentDAO.findById(id);
		if (environment.equals(null))
			throw new CheckException("Environment variable doesn't exist");
		return environment;
	}

	@Override
	public List<EnvironmentVariable> loadEnvironnmentsByContainer(String containerName) throws ServiceException {
		List<EnvironmentVariable> environmentVariables = environmentDAO.findByContainer(containerName);
		return environmentVariables;
	}

	@Override
	@Transactional
	public void delete(int id) {
		Server server = null;
		Application application = null;
		try {
			EnvironmentVariable environmentVariable = loadEnvironnment(id);
			environmentDAO.delete(environmentVariable);
		} catch (CheckException e) {
			throw new CheckException(e.getMessage());
		} catch (ServiceException e) {
			throw new CheckException(e.getMessage());
		} finally {
			applicationEventPublisher.publishEvent(new ServerStartEvent(server));
			applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
		}

	}

	@Override
	@Transactional
	public EnvironmentVariable update(User user, EnvironmentVariable environmentVariable, String applicationName,
			String containerName, Integer id) {
		checkEnvironmentVariableConsistence(environmentVariable, containerName);
		Server server = null;
		Application application = null;
		try {
			loadEnvironnment(id);
			server = serverService.findByName(containerName);
			application = applicationService.findByNameAndUser(user, applicationName);
			stopAndRemoveServer(server, application);
			environmentVariable.setId(id);
			environmentVariable = environmentDAO.save(environmentVariable);
			recreateAndMountVolumes(server, application);
		} catch (CheckException e) {
			throw new CheckException(e.getMessage());
		} catch (ServiceException e) {
			throw new CheckException(e.getMessage());
		} finally {
			applicationEventPublisher.publishEvent(new ServerStartEvent(server));
			applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
		}
		return environmentVariable;
	}

	private void checkEnvironmentVariableConsistence(EnvironmentVariable environmentVariable, String containerName) {
		if (environmentVariable.getKeyEnv() == null || environmentVariable.getKeyEnv().isEmpty())
			throw new CheckException("This key is not consistent !");
		if (!environmentVariable.getKeyEnv().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This key is not consistent : " + environmentVariable.getKeyEnv());
		List<EnvironmentVariable> environmentList = environmentDAO.findByContainer(containerName);
		Optional<EnvironmentVariable> value = environmentList.stream()
				.filter(v -> v.getKeyEnv().equals(environmentVariable.getKeyEnv())).findFirst();
		if (value.isPresent())
			throw new CheckException("This key already exists");
	}

	private void stopAndRemoveServer(Server server, Application application) throws ServiceException {
		applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
		applicationEventPublisher.publishEvent(new ServerStopEvent(server));
		dockerService.removeServer(server.getName(), false);
	}

	private void recreateAndMountVolumes(Server server, Application application) throws ServiceException {
		List<String> volumes = volumeService.loadAllByContainerName(server.getName())
				.stream().map(v -> v.getName() + ":" + v.getVolumeAssociations().stream().findFirst().get().getPath()
						+ ":" + v.getVolumeAssociations().stream().findFirst().get().getMode())
				.collect(Collectors.toList());
		List<String> envs = loadEnvironnmentsByContainer(server.getName()).stream()
				.map(e -> e.getKeyEnv() + "=" + e.getValueEnv()).collect(Collectors.toList());
		dockerService.createServer(server.getName(), server, server.getImage().getPath(),
				server.getApplication().getUser(), envs, false, volumes);
		server = serverService.startServer(server);
		serverService.addCredentialsForServerManagement(server, server.getApplication().getUser());
	}
}

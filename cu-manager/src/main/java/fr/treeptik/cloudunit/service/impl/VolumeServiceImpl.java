package fr.treeptik.cloudunit.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStopEvent;
import fr.treeptik.cloudunit.dao.VolumeDAO;
import fr.treeptik.cloudunit.docker.core.DockerCloudUnitClient;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.service.VolumeService;

@Service
public class VolumeServiceImpl implements VolumeService {

	@Inject
	private VolumeDAO volumeDAO;

	@Inject
	private DockerCloudUnitClient dockerCloudUnitClient;

	@Inject
	private DockerService dockerService;

	@Inject
	private ServerService serverService;

	@Inject
	private ApplicationEventPublisher publisher;

	@Override
	@Transactional
	public void createNewVolume(Volume volume, Application application, String containerName)
			throws ServiceException, CheckException {
		if (volume.getName() == null || volume.getName().isEmpty())
			throw new CheckException("This name is not consistent !");
		if (volume.getPath() == null || volume.getPath().isEmpty())
			throw new CheckException("This path is not consistent !");
		if (!volume.getName().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This name is not consistent : " + volume.getName());
		if (loadAllVolumes().stream().filter(v -> v.getName().equals(volume.getName())).findAny().isPresent()) {
			throw new CheckException("This name already exists");
		}
		Server server = serverService.findByContainerID(containerName);
		publisher.publishEvent(new ApplicationPendingEvent(application));
		volume.setApplication(application);
		volume.setContainerName(containerName);
		dockerCloudUnitClient.createVolume(volume.getName(), "runtime");
		volumeDAO.save(volume);
		publisher.publishEvent(new ServerStopEvent(server));
		dockerService.removeServer(server.getName(), false);
		List<String> volumes = loadVolumeByContainer(server.getContainerID()).stream()
				.map(t -> t.getName() + ":" + t.getPath() + ":rw").collect(Collectors.toList());
		dockerService.createServer(server.getName(), server, server.getImage().getPath(),
				server.getApplication().getUser(), null, false, volumes);
		server = serverService.startServer(server);
		serverService.addCredentialsForServerManagement(server, server.getApplication().getUser());

		publisher.publishEvent(new ServerStartEvent(server));

		publisher.publishEvent(new ApplicationStartEvent(application));

	}

	@Override
	@Transactional
	public void updateVolume(Volume volume, Application application, String containerName)
			throws ServiceException, CheckException {

		if (volume.getName() == null || volume.getName().isEmpty())
			throw new CheckException("This name is not consistent !");
		if (volume.getPath() == null || volume.getPath().isEmpty())
			throw new CheckException("This path is not consistent !");
		if (!volume.getName().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This name is not consistent : " + volume.getName());
		if (loadAllVolumes().stream().filter(v -> v.getName().equals(volume.getName())).findAny().isPresent()) {
			throw new CheckException("This name already exists");
		}
		volume.setApplication(application);
		volume.setContainerName(containerName);
		dockerCloudUnitClient.removeVolume(loadVolume(volume.getId()).getName());
		dockerCloudUnitClient.createVolume(volume.getName(), "runtime");
		volumeDAO.save(volume);

	}

	@Override
	public Volume loadVolume(int id) throws CheckException {
		Volume volume = volumeDAO.findById(id);
		if (volume.equals(null))
			throw new CheckException("Volume doesn't exist");
		return volume;
	}

	@Override
	public List<Volume> loadVolumeByApplication(String applicationName) {
		return volumeDAO.findByApplicationName(applicationName);
	}

	@Override
	public List<Volume> loadVolumeByContainer(String containerName) {
		return volumeDAO.findByContainer(containerName);
	}

	@Override
	public List<Volume> loadAllVolumes() {
		return volumeDAO.findAllVolumes();
	}

	@Override
	@Transactional
	public void delete(int id) throws CheckException {
		Volume volume = loadVolume(id);
		dockerCloudUnitClient.removeVolume(volume.getName());
		volumeDAO.delete(id);
	}
}

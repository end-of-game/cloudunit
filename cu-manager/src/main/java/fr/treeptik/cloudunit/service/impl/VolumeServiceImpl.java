package fr.treeptik.cloudunit.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.dao.VolumeDAO;
import fr.treeptik.cloudunit.docker.core.DockerCloudUnitClient;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.service.VolumeService;

@Service
public class VolumeServiceImpl implements VolumeService {

	@Inject
	private VolumeDAO volumeDAO;

	@Inject
	private DockerCloudUnitClient dockerCloudUnitClient;

	@Override
	@Transactional
	public Volume createNewVolume(Volume volume) {
		try {
			checkVolumeFormat(volume);
			dockerCloudUnitClient.createVolume(volume.getName(), "runtime");
			volume = volumeDAO.save(volume);
			return volume;
		} catch (CheckException e) {
			throw new CheckException(e.getMessage());
		}

	}

	@Override
	@Transactional
	public Volume updateVolume(Volume volume) {
		try {
			checkVolumeFormat(volume);
			Volume currentVolume = loadVolume(volume.getId());
			dockerCloudUnitClient.removeVolume(currentVolume.getName());
			dockerCloudUnitClient.createVolume(volume.getName(), "runtime");
			volumeDAO.save(volume);
			return volume;
		} catch (CheckException e) {
			throw new CheckException(e.getMessage());
		}

	}

	@Override
	@Transactional
	public void delete(int id) {
		try {
			Volume volume = loadVolume(id);
			volumeDAO.delete(id);
			dockerCloudUnitClient.removeVolume(volume.getName());
		} catch (CheckException e) {
			throw new CheckException(e.getMessage());
		}
	}

	@Override
	public Volume loadVolume(int id) throws CheckException {
		Volume volume = volumeDAO.findById(id);
		if (volume.equals(null))
			throw new CheckException("Volume doesn't exist");
		return volume;
	}

	@Override
	public List<Volume> loadAllVolumes() {
		return volumeDAO.findAllVolumes();
	}

	private void checkVolumeFormat(Volume volume) {
		if (volume.getName() == null || volume.getName().isEmpty())
			throw new CheckException("This name is not consistent !");
		if (!volume.getName().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This name is not consistent : " + volume.getName());
		if (loadAllVolumes().stream().filter(v -> v.getName().equals(volume.getName())).findAny().isPresent()) {
			throw new CheckException("This name already exists");
		}
	}

}

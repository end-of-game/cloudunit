package fr.treeptik.cloudunit.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.dao.VolumeDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.service.VolumeService;

@Service
public class VolumeServiceImpl implements VolumeService {

	@Inject
	private VolumeDAO volumeDAO;

	@Override
	@Transactional
	public void save(Volume volume, Application application, String containerId)
			throws ServiceException, CheckException {

		if (volume.getName() == null || volume.getName().isEmpty())
			throw new CheckException("This name is not consistent !");

		if (volume.getPath() == null || volume.getPath().isEmpty())
			throw new CheckException("This path is not consistent !");

		if (!volume.getName().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This name is not consistent : " + volume.getName());
		List<Volume> volumes = loadAllVolumes();
		for (Volume v : volumes) {
			if (volume.getId() != null && v.getName().equals(volume.getName()) && !v.getName().equals(volume.getName()))
				throw new CheckException("This name already exists");
			if (volume.getId() == null && v.getName().equals(volume.getName()))
				throw new CheckException("This name already exists");
		}
		if (volume.equals(null))
			throw new CheckException("Volume doesn't exist");
		volume.setApplication(application);
		volume.setContainerId(containerId);
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
	public List<Volume> loadVolumeByContainer(String containerId) {
		return volumeDAO.findByContainer(containerId);
	}

	@Override
	public List<Volume> loadAllVolumes() {
		return volumeDAO.findAllVolumes();
	}

	@Override
	@Transactional
	public void delete(int id) throws CheckException {
		loadVolume(id);
		volumeDAO.delete(id);
	}
}

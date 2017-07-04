package fr.treeptik.cloudunit.service.impl;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.dao.VolumeAssociationDAO;
import fr.treeptik.cloudunit.dao.VolumeDAO;
import fr.treeptik.cloudunit.docker.core.DockerCloudUnitClient;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.model.VolumeAssociation;
import fr.treeptik.cloudunit.service.VolumeService;

@Service
public class VolumeServiceImpl implements VolumeService {

	@Inject
	private VolumeDAO volumeDAO;

	@Inject
	private VolumeAssociationDAO volumeAssociationDAO;

	@Inject
	private DockerCloudUnitClient dockerCloudUnitClient;

	@Override
	@Transactional
	public Volume createNewVolume(String name) {
		try {
			checkVolumeFormat(name);
			dockerCloudUnitClient.createVolume(name, "runtime");
			return registerNewVolume(name);
		} catch (CheckException e) {
			throw new CheckException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Volume registerNewVolume(String name) {
		try {
			Volume volume = new Volume(name);
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
			checkVolumeFormat(volume.getName());
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
		Volume volume = null;
		try {
			volume = loadVolume(id);
			if(volume != null && volume.getVolumeAssociations().size() != 0) {
				throw new CheckException("Volume couldn't be remove because it's currently linked whith application");
			}
			volumeDAO.delete(id);
			dockerCloudUnitClient.removeVolume(volume.getName());
		} catch (CheckException e) {
			throw new CheckException(volume.toString(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Volume loadVolume(int id) throws CheckException {
		Volume volume = volumeDAO.findById(id);
		if (volume == null)
			throw new CheckException("Volume doesn't exist");
		return volume;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Volume> loadAllVolumes() {
		return volumeDAO.findAllVolumes();
	}

	private void checkVolumeFormat(String name) {
		if (name == null || name.isEmpty())
			throw new CheckException("This name is not consistent !");
		if (!name.matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This name is not consistent : " + name);
		if (loadAllVolumes().stream().filter(v -> v.getName().equals(name)).findAny().isPresent()) {
			throw new CheckException("This name already exists");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Volume> loadAllByContainerName(String containerName) throws ServiceException {
		return volumeDAO.findVolumesByContainerName(containerName);
	}

	@Override
	public Volume findByName(String name) {
		return volumeDAO.findByName(name);
	}

	@Override
	@Transactional
	public VolumeAssociation saveAssociation(VolumeAssociation volumeAssociation) {
		return volumeAssociationDAO.save(volumeAssociation);
	}

	@Override
	@Transactional
	public void removeAssociation(VolumeAssociation volumeAssociation) {
		volumeAssociationDAO.delete(volumeAssociation);
	}

	@Override
	@Transactional
	public Set<VolumeAssociation> loadVolumeAssociations(int id) {
		return loadVolume(id).getVolumeAssociations();
	}

}

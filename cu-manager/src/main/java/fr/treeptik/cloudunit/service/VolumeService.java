package fr.treeptik.cloudunit.service;

import java.util.List;
import java.util.Set;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.model.VolumeAssociation;

public interface VolumeService {

	Volume loadVolume(int id) throws ServiceException, CheckException;

	Volume findByName(String name);

	List<Volume> loadAllVolumes() throws ServiceException;

	List<Volume> loadAllByContainerName(String containerName) throws ServiceException;

	void delete(int id) throws ServiceException, CheckException;

	Volume updateVolume(Volume volume) throws ServiceException, CheckException;

	Volume createNewVolume(String name) throws ServiceException;
	
	Volume registerNewVolume(String name) throws ServiceException;

	VolumeAssociation saveAssociation(VolumeAssociation volumeAssociation);

	void removeAssociation(VolumeAssociation volumeAssociation);

	Set<VolumeAssociation> loadVolumeAssociations(int id);

}

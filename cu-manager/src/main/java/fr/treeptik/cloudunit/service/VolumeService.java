package fr.treeptik.cloudunit.service;

import java.util.List;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Volume;

public interface VolumeService {

	Volume loadVolume(int id) throws ServiceException, CheckException;

	List<Volume> loadVolumeByApplication(String applicationName) throws ServiceException;

	List<Volume> loadVolumeByContainer(String containerId) throws ServiceException;

	List<Volume> loadAllVolumes() throws ServiceException;

	void delete(int id) throws ServiceException, CheckException;

	Volume updateVolume(Volume volume) throws ServiceException, CheckException;

	Volume createNewVolume(Volume volume) throws ServiceException;

}

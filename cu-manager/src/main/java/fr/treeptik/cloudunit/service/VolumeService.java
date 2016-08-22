package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Volume;

import java.util.List;

public interface VolumeService {

	void createNewVolume(Volume volume, Application application, String containerId)
			throws ServiceException, CheckException;

	Volume loadVolume(int id) throws ServiceException, CheckException;

	List<Volume> loadVolumeByApplication(String applicationName) throws ServiceException;

	List<Volume> loadVolumeByContainer(String containerId) throws ServiceException;

	List<Volume> loadAllVolumes() throws ServiceException;

	void delete(int id) throws ServiceException, CheckException;

	void updateVolume(Volume volume, Application application, String containerId)
			throws ServiceException, CheckException;

}

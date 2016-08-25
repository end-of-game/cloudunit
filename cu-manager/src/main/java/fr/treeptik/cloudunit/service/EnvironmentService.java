package fr.treeptik.cloudunit.service;

import java.util.List;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.User;

public interface EnvironmentService {
	EnvironmentVariable save(User user, EnvironmentVariable environmentVariableRequest, String applicationName,
			String containerId) throws ServiceException, CheckException;

	EnvironmentVariable loadEnvironnment(int id) throws ServiceException, CheckException;

	List<EnvironmentVariable> loadEnvironnmentsByContainer(String containerId) throws ServiceException;

	EnvironmentVariable update(User user, EnvironmentVariable environmentVariableRequest, String applicationName,
			String containerId, Integer id) throws ServiceException, CheckException;

	void delete(User user, int id, String containerName, String applicationName) throws ServiceException;
}

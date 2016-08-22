package fr.treeptik.cloudunit.service;

import java.util.List;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.User;

public interface EnvironmentService {
	EnvironmentVariable save(User user, EnvironmentVariable environmentVariableRequest,
                                    String applicationName, String containerId)
            throws ServiceException, CheckException;

	EnvironmentVariable loadEnvironnment(int id) throws ServiceException, CheckException;

    List<EnvironmentVariable> loadEnvironnmentsByContainer(String containerId) throws ServiceException;

    void delete(int id) throws ServiceException, CheckException;

    EnvironmentVariable update(User user, EnvironmentVariable environmentVariableRequest,
                                      String applicationName, String containerId, Integer id)
            throws ServiceException, CheckException;
}

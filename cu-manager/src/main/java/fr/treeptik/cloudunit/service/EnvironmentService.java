package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.EnvironmentVariableRequest;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Environment;
import fr.treeptik.cloudunit.model.User;

import java.util.List;

public interface EnvironmentService {
    EnvironmentVariableRequest save(User user, EnvironmentVariableRequest environmentVariableRequest,
                                    String applicationName, String containerId)
            throws ServiceException, CheckException;

    EnvironmentVariableRequest loadEnvironnment(int id) throws ServiceException, CheckException;

    List<EnvironmentVariableRequest> loadEnvironnmentsByContainer(String containerId) throws ServiceException;

    void delete(int id) throws ServiceException, CheckException;

    EnvironmentVariableRequest update(User user, EnvironmentVariableRequest environmentVariableRequest,
                                      String applicationName, String containerId, Integer id)
            throws ServiceException, CheckException;
}

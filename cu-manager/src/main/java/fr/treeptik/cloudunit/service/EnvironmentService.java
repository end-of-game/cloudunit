package fr.treeptik.cloudunit.service;

import java.util.List;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.User;

public interface EnvironmentService {

    EnvironmentVariable update(User user, EnvironmentVariable environmentVariable, String applicationName,
            String containerName, Integer id) throws ServiceException;

    EnvironmentVariable loadEnvironnment(int id) throws ServiceException, CheckException;

    List<EnvironmentVariable> loadEnvironnmentsByContainer(String containerName) throws ServiceException;

    void delete(User user, int id, String applicationName, String containerName) throws ServiceException;

    void save(User user, List<EnvironmentVariable> environments, String applicationName, String containerName)
            throws ServiceException;

    EnvironmentVariable save(User user, EnvironmentVariable environment, String applicationName, String containerName)
            throws ServiceException;

    void createInDatabase(List<EnvironmentVariable> environments, String containerName, Application application);

    void delete(User user, List<EnvironmentVariable> envs, String applicationName, String containerName)
            throws ServiceException;

}

package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Environment;

import java.util.List;

public interface EnvironmentService {
    void save(Environment environment) throws ServiceException;

    Environment loadEnvironnment(int id) throws ServiceException;

    List<Environment> loadEnvironnmentsByApplication(String applicationName) throws ServiceException;

    List<Environment> loadEnvironnmentsByContainer(String containerId) throws ServiceException;

    List<Environment> loadAllEnvironnments() throws ServiceException;

    void delete(int id) throws ServiceException;
}

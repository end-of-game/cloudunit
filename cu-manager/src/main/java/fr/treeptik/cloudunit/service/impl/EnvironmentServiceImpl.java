package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.EnvironmentDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Environment;
import fr.treeptik.cloudunit.service.EnvironmentService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

    @Inject
    private EnvironmentDAO environmentDAO;

    @Override
    public void save(Environment environment) throws ServiceException {
        environmentDAO.save(environment);
    }

    @Override
    public Environment loadEnvironnment(int id) throws ServiceException {
        return environmentDAO.findById(id);
    }

    @Override
    public List<Environment> loadEnvironnmentsByApplication(String applicationName) throws ServiceException {
        List<Environment> environments = environmentDAO.findByApplicationName(applicationName);
        return environments;
    }

    @Override
    public List<Environment> loadAllEnvironnments() throws ServiceException {
        return environmentDAO.findAllEnvironnments();
    }

    @Override
    public void delete(int id) throws ServiceException {
        environmentDAO.delete(id);
    }
}

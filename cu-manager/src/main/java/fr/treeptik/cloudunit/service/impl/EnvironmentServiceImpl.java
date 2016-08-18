package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.EnvironmentDAO;
import fr.treeptik.cloudunit.dto.EnvironmentVariableRequest;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Environment;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

    @Inject
    private EnvironmentDAO environmentDAO;

    @Inject
    private ApplicationService applicationService;

    public EnvironmentVariableRequest save(User user, EnvironmentVariableRequest environmentVariableRequest,
                                           String applicationName, String containerId)
            throws ServiceException, CheckException {
        if (environmentVariableRequest.getKey() == null || environmentVariableRequest.getKey().isEmpty())
            throw new CheckException("This key is not consistent !");

        if (!environmentVariableRequest.getKey().matches("^[-a-zA-Z0-9_]*$"))
            throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKey());

        List<Environment> environmentList = loadAllEnvironnments();
        for (Environment environment : environmentList)
            if (environment.getKeyEnv().equals(environmentVariableRequest.getKey()))
                throw new CheckException("This key already exists");

        Application application = applicationService.findByNameAndUser(user, applicationName);
        Environment environment = new Environment();

        environment.setApplication(application);
        environment.setContainerId(containerId);
        environment.setKeyEnv(environmentVariableRequest.getKey());
        environment.setValueEnv(environmentVariableRequest.getValue());

        environmentDAO.save(environment);

        EnvironmentVariableRequest environmentVariableRequest1 = new EnvironmentVariableRequest();
        List<EnvironmentVariableRequest> environmentVariableRequestList = loadEnvironnmentsByContainer(containerId);
        for (EnvironmentVariableRequest environmentVariableRequest2 : environmentVariableRequestList)
            if (environmentVariableRequest2.getKey().equals(environment.getKeyEnv())) {
                environmentVariableRequest1.setId(environmentVariableRequest2.getId());
                environmentVariableRequest1.setKey(environmentVariableRequest2.getKey());
                environmentVariableRequest1.setValue(environmentVariableRequest2.getValue());
            }

        return environmentVariableRequest1;
    }

    @Override
    public EnvironmentVariableRequest loadEnvironnment(int id) throws ServiceException, CheckException {

        Environment environment = environmentDAO.findById(id);

        if (environment.equals(null))
            throw new CheckException("Environment variable doesn't exist");

        EnvironmentVariableRequest environmentVariableRequest = new EnvironmentVariableRequest();
        environmentVariableRequest.setId(environment.getId());
        environmentVariableRequest.setKey(environment.getKeyEnv());
        environmentVariableRequest.setValue(environment.getValueEnv());

        return environmentVariableRequest;
    }

    @Override
    public List<EnvironmentVariableRequest> loadEnvironnmentsByContainer(String containerId) throws ServiceException {
        List<EnvironmentVariableRequest> environmentVariableRequestList = new ArrayList<>();
        List<Environment> environmentList = environmentDAO.findByContainer(containerId);
        for (Environment environment : environmentList) {
            EnvironmentVariableRequest environmentVariableRequest = new EnvironmentVariableRequest();
            environmentVariableRequest.setId(environment.getId());
            environmentVariableRequest.setKey(environment.getKeyEnv());
            environmentVariableRequest.setValue(environment.getValueEnv());
            environmentVariableRequestList.add(environmentVariableRequest);
        }

        return environmentVariableRequestList;
    }

    public List<Environment> loadAllEnvironnments() throws ServiceException {
        return environmentDAO.findAllEnvironnments();
    }

    @Override
    public void delete(int id) throws ServiceException, CheckException {
        Environment environment = environmentDAO.findById(id);

        if (environment.equals(null)) {
            throw new CheckException("Environment variable doesn't exist");
        }

        environmentDAO.delete(id);
    }

    public EnvironmentVariableRequest update(User user, EnvironmentVariableRequest environmentVariableRequest,
                                             String applicationName, String containerId, Integer id) throws ServiceException, CheckException {
        if (environmentVariableRequest.getKey() == null || environmentVariableRequest.getKey().isEmpty())
            throw new CheckException("This key is not consistent !");

        if (!environmentVariableRequest.getKey().matches("^[-a-zA-Z0-9_]*$"))
            throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKey());

        Environment environment = environmentDAO.findById(id);
        List<Environment> environmentList = loadAllEnvironnments();
        for (Environment environment1 : environmentList)
            if (environment1.getKeyEnv().equals(environmentVariableRequest.getKey())
                    && !environment1.getKeyEnv().equals(environment.getKeyEnv()))
                throw new CheckException("This key already exists");

        if (environment.equals(null))
            throw new CheckException("Environment variable doesn't exist");

        environment.setKeyEnv(environmentVariableRequest.getKey());
        environment.setValueEnv(environmentVariableRequest.getValue());

        environmentDAO.save(environment);

        EnvironmentVariableRequest returnEnv = new EnvironmentVariableRequest();
        returnEnv.setId(environment.getId());
        returnEnv.setKey(environment.getKeyEnv());
        returnEnv.setValue(environment.getValueEnv());

        return returnEnv;
    }
}

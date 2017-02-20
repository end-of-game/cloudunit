package fr.treeptik.cloudunit.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ModuleStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.dao.EnvironmentDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.service.VolumeService;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

    private Logger logger = LoggerFactory.getLogger(EnvironmentServiceImpl.class);

    @Inject
    private EnvironmentDAO environmentDAO;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;

    @Inject
    private VolumeService volumeService;

    @Inject
    private DockerService dockerService;

    @Inject
    private ServerService serverService;

    @Inject
    private ModuleService moduleService;

    @Override
    @Transactional
    @CacheEvict(value = "env", allEntries = true)
    public EnvironmentVariable save(User user, EnvironmentVariable environment, String applicationName,
            String containerName) throws ServiceException {
        checkEnvironmentVariableConsistence(environment, containerName);
        Application application = null;
        try {
            application = applicationService.findByNameAndUser(user, applicationName);
            stopAndRemoveContainer(containerName, application);
            environment.setApplication(application);
            environment.setContainerName(containerName);
            environment = environmentDAO.save(environment);
            recreateAndMountVolumes(containerName, application);
        } catch (CheckException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("environment:[").append(environment).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new CheckException(e.getMessage(), e);
        } catch (ServiceException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("environment:[").append(environment).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new ServiceException(e.getMessage(), e);
        }
        return environment;
    }

    @Override
    @Transactional
    @CacheEvict(value = "env", allEntries = true)
    public void save(User user, List<EnvironmentVariable> environments, String applicationName, String containerName)
            throws ServiceException {
        environments.stream().forEach(e -> checkEnvironmentVariableConsistence(e, containerName));
        final Application application = applicationService.findByNameAndUser(user, applicationName);
        try {
            stopAndRemoveContainer(containerName, application);
            createInDatabase(environments, containerName, application);
            recreateAndMountVolumes(containerName, application);
        } catch (CheckException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("environments:[").append(environments).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new CheckException(e.getMessage(), e);
        } catch (ServiceException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("environment:[").append(environments).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public void createInDatabase(List<EnvironmentVariable> environments, String containerName,
            final Application application) {
        environments.stream().forEach(e -> {
            e.setApplication(application);
            e.setContainerName(containerName);
            environmentDAO.save(e);
        });
    }

    @Override
    public EnvironmentVariable loadEnvironnment(int id) throws ServiceException, CheckException {
        EnvironmentVariable environment = environmentDAO.findById(id);
        if (environment.equals(null))
            throw new CheckException("Environment variable doesn't exist");
        return environment;
    }

    @Override
    public List<EnvironmentVariable> loadEnvironnmentsByContainer(String containerName) throws ServiceException {
        List<EnvironmentVariable> environmentVariables = environmentDAO.findByContainer(containerName);
        return environmentVariables;
    }

    @Override
    @Transactional
    @CacheEvict(value = "env", allEntries = true)
    public void delete(User user, int id, String applicationName, String containerName) throws ServiceException {
        Server server = null;
        Application application = null;
        try {
            server = serverService.findByName(containerName);
            application = applicationService.findByNameAndUser(user, applicationName);
            stopAndRemoveContainer(containerName, application);
            EnvironmentVariable environmentVariable = loadEnvironnment(id);
            environmentDAO.delete(environmentVariable);
            recreateAndMountVolumes(containerName, application);
        } catch (CheckException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("id:[").append(id).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new CheckException(e.getMessage(), e);
        } catch (ServiceException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("id:[").append(id).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new ServiceException(e.getMessage(), e);
        } finally {
            applicationEventPublisher.publishEvent(new ServerStartEvent(server));
            applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "env", allEntries = true)
    public void delete(User user, List<EnvironmentVariable> envs, String applicationName, String containerName)
            throws ServiceException {
        Server server = null;
        Application application = null;
        try {
            server = serverService.findByName(containerName);
            application = applicationService.findByNameAndUser(user, applicationName);
            stopAndRemoveContainer(containerName, application);
            envs.stream().forEach(e -> environmentDAO.delete(e));
            recreateAndMountVolumes(containerName, application);
        } catch (CheckException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new CheckException(e.getMessage(), e);
        } catch (ServiceException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new ServiceException(e.getMessage(), e);
        } finally {
            applicationEventPublisher.publishEvent(new ServerStartEvent(server));
            applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
        }

    }

    @Override
    @Transactional
    @CacheEvict(value = "env", allEntries = true)
    public EnvironmentVariable update(User user, EnvironmentVariable environmentVariable, String applicationName,
            String containerName) throws ServiceException {
        checkEnvironmentVariableConsistence(environmentVariable, containerName);
        Application application = null;
        try {
            application = applicationService.findByNameAndUser(user, applicationName);
            stopAndRemoveContainer(containerName, application);
            environmentVariable = environmentDAO.save(environmentVariable);
            recreateAndMountVolumes(containerName, application);
        } catch (CheckException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("environmentVariable:[").append(environmentVariable).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            throw new CheckException(e.getMessage(), e);
        } catch (ServiceException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("environmentVariable:[").append(environmentVariable).append("]");
            msgError.append(", applicationName:[").append(applicationName).append("]");
            msgError.append(", containerName:[").append(containerName).append("]");
            logger.error(msgError.toString());
            throw new ServiceException(e.getMessage(), e);
        } finally {

            applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
        }
        return environmentVariable;
    }

    private void checkEnvironmentVariableConsistence(EnvironmentVariable environmentVariable, String containerName) {
        if (environmentVariable.getKey() == null || environmentVariable.getKey().isEmpty())
            throw new CheckException("This key is not consistent !");
        if (!environmentVariable.getKey().matches("^[-a-zA-Z0-9_]*$"))
            throw new CheckException("This key is not consistent : " + environmentVariable.getKey());
        List<EnvironmentVariable> environmentList = environmentDAO.findByContainer(containerName);
        Optional<EnvironmentVariable> value = environmentList.stream()
                .filter(v -> v.getKey().equals(environmentVariable.getKey())).findFirst();
        if (value.isPresent())
            throw new CheckException("This key already exists");
    }

    private void stopAndRemoveContainer(String containerName, Application application) throws ServiceException {
        applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
        dockerService.removeContainer(containerName, false);
    }

    private void recreateAndMountVolumes(String containerName, Application application) throws ServiceException {
        List<String> volumes = volumeService.loadAllByContainerName(containerName)
                .stream().map(v -> v.getName() + ":" + v.getVolumeAssociations().stream().findFirst().get().getPath()
                        + ":" + v.getVolumeAssociations().stream().findFirst().get().getMode())
                .collect(Collectors.toList());
        List<String> envs = loadEnvironnmentsByContainer(containerName).stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
        Server server = serverService.findByName(containerName);
        if (server != null) {
            dockerService.createServer(server.getName(), server, server.getImage().getPath(), server.getImage().getImageSubType().toString(),
                    server.getApplication().getUser(), envs, false, volumes);
            server = serverService.startServer(server);
            applicationEventPublisher.publishEvent(new ServerStartEvent(server));
        } else {
            Module module = moduleService.findByName(containerName);
            dockerService.createModule(module.getName(), module, module.getImage().getPath(),
                    module.getApplication().getUser(), envs, false, volumes);
            applicationEventPublisher.publishEvent(new ModuleStartEvent(module));
        }
    }
}

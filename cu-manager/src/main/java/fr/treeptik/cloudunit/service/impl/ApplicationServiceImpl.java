/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.service.impl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Deployment;
import fr.treeptik.cloudunit.model.DeploymentType;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DeploymentService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.FileService;
import fr.treeptik.cloudunit.service.ImageService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.NamingUtils;

@Service
@DependsOn("dataSourceInitializer")
public class ApplicationServiceImpl implements ApplicationService {

	Locale locale = Locale.ENGLISH;

	private Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

	@Inject
	private ApplicationDAO applicationDAO;

	@Inject
	private ServerService serverService;

	@Inject
	private DeploymentService deploymentService;

	@Inject
	private ModuleService moduleService;

	@Inject
	private ImageService imageService;

	@Inject
	private FileService fileService;

	@Inject
	private DockerService dockerService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private ApplicationEventPublisher applicationEventPublisher;

	@Inject
	private MessageSource messageSource;

	@Value("${docker.manager.ip:192.168.50.4:4243}")
	private String dockerSocketIP;

	@Value("${suffix.cloudunit.io}")
	private String suffixCloudUnitIO;

	@Value("${java.version.default}")
	private String javaVersionDefault;

	@Value("${cloudunit.instance.name}")
	private String cuInstanceName;

	private List<String> imageNames;

	@PostConstruct
	public void init() throws ServiceException {
		logger.info("Loading images enabled from database...");
	    List<Image> imagesEnabled = imageService.findEnabledImages();
        imageNames = imagesEnabled.stream().map(i -> i.getName()).collect(Collectors.toList());
		logger.info("{} images have been loaded from database", imageNames.size());
    }

    /**
	 * Test if the user can create new applications because we limit the number
	 * per user
	 *
	 * @param application
	 * @throws CheckException
	 * @throws ServiceException
	 */
	public void checkCreate(User user, String application) throws CheckException, ServiceException {
		try {
			if (checkAppExist(user, application)) {
				throw new CheckException(messageSource.getMessage("app.exists", null, locale));
			}
			if (checkNameLength(application)) {
				throw new CheckException("This name has length equal to zero : " + application);
			}
		} catch (PersistenceException e) {
			logger.error("ApplicationService Error : Create Application" + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Test if the application already exists
	 *
	 * @param user
	 * @param applicationName
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@Override
	public boolean checkAppExist(User user, String applicationName) throws ServiceException, CheckException {
		logger.info("--CHECK APP EXIST--");
		if (applicationDAO.findByNameAndUser(user.getId(), applicationName, cuInstanceName) == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean checkNameLength(String applicationName) {
		if (applicationName.length() == 0)
			return true;
		return false;
	}

	/**
	 * Save app in just in DB, not create container use principally to charge
	 * status.PENDING of entity until it's really functionnal
	 */
	@Override
	@Transactional
	public Application saveInDB(Application application) throws ServiceException {
		logger.debug("-- SAVE -- : " + application);
		// Do not affect application with save return.
		// You could lose the relationships.
		applicationDAO.save(application);
		return application;
	}


	@Override
	@Transactional
	public Application create(String applicationName, String imageName)
			throws ServiceException, CheckException {

        User user = authentificationUtils.getAuthentificatedUser();
        if (!imageNames.contains(imageName)) {
            throw new CheckException(messageSource.getMessage("server.not.found", null, locale));
        }

        Image image = imageService.findByName(imageName);
		Application application = Application.of(applicationName, image)
                .withDisplayName(applicationName)
                .withUser(user)
                .withSuffixCloudUnitIO(suffixCloudUnitIO)
                .withManagerIp(dockerSocketIP)
                .withCuInstanceName(cuInstanceName).build();

		checkCreate(user, applicationName);

        application = applicationDAO.save(application);

        Server server = application.getServer();

        server = serverService.create(server);
        
        application = applicationDAO.save(application);
        applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));

		logger.info("ApplicationService : Application " + application.getName() + " successfully created.");

		return application;
	}

	/**
	 * Remove an application
	 *
	 * @param application
	 * @param user
	 * @return
	 * @throws ServiceException
	 */
	@Override
	@Transactional
	@CacheEvict(value = "env", allEntries = true)
	public Application remove(Application application, User user) throws ServiceException, CheckException {

		try {
			logger.info("Starting removing application " + application.getName());

			// Delete all modules
			List<Module> listModules = application.getModules();
			for (Module module : listModules) {
				try {
					moduleService.remove(user, module, false, application.getStatus());
				} catch (ServiceException | CheckException e) {
					application.setStatus(Status.FAIL);
					logger.error("ApplicationService Error : failed to remove module " + module.getName()
							+ " for application " + application.getName() + " : " + e);
					e.printStackTrace();
				}
			}

			Server server = application.getServer();
			serverService.remove(server.getName());

			application.removeServer();
			applicationDAO.delete(application);

			logger.info("ApplicationService : Application successfully removed ");

		} catch (PersistenceException e) {
			setStatus(application, Status.FAIL);
			logger.error("ApplicationService Error : failed to remove " + application.getName() + " : " + e);

			throw new ServiceException(e.getLocalizedMessage(), e);
		} catch (ServiceException e) {
			setStatus(application, Status.FAIL);
			logger.error(
					"ApplicationService Error : failed to remove application " + application.getName() + " : " + e);
			e.printStackTrace();
		} catch (CheckException e) {
			e.printStackTrace();
		}
		return application;
	}

	/**
	 * Methode permettant de mettre l'application dans un état particulier pour
	 * se prémunir d'éventuel problème de concurrence au niveau métier
	 */
	@Override
	@Transactional
	public void setStatus(Application application, Status status) throws ServiceException {
		try {
			Application _application = applicationDAO.findOne(application.getId());
			_application.setStatus(status);
			application.setStatus(status);
			applicationDAO.saveAndFlush(_application);
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional
	public Application start(Application application) throws ServiceException {
		try {
			logger.debug("start : Methods parameters : " + application);

			application.getModules().stream().forEach(m -> {
				try {
					moduleService.startModule(m.getName());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			});
			Server server = application.getServer();
			logger.info("old server ip : " + server.getContainerIP());
			server = serverService.startServer(server);

			logger.info("ApplicationService : Application successfully started ");
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return application;
	}

	@Override
	@Transactional
	public Application stop(Application application) throws ServiceException {
		try {
			Server server = application.getServer();
			serverService.stopServer(server);
			application.getModules().stream().forEach(m -> {
				try {
					moduleService.stopModule(m.getName());
				} catch (ServiceException e) {
					logger.error(application.toString(), e);
				}
			});
			logger.info("ApplicationService : Application successfully stopped ");
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return application;
	}

	/**
	 * Method useful for Logs and Monitoring Management
	 *
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public List<Application> findAll() throws ServiceException {
		try {
			logger.debug("start findAll");
			List<Application> listApplications = applicationDAO.findAll();
			for (Application application : listApplications) {
				application.setServer(serverService.findByApp(application));
				application.setModules(moduleService.findByAppAndUser(application.getUser(), application.getName()));
			}
			logger.debug("ApplicationService : All Applications found ");
			return listApplications;
		} catch (PersistenceException e) {
			logger.error("Error ApplicationService : error findAll Method : " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Application> findAllByUser(User user) throws ServiceException {
		try {
			List<Application> applications = applicationDAO.findAllByUser(user.getId(), cuInstanceName);
			logger.debug("ApplicationService : All Applications found ");
			return applications;
		} catch (PersistenceException e) {
			logger.error("Error ApplicationService : error findById Method : " + user);
			throw new ServiceException(user.toString(), e);
		}
	}

	@Override
	public Application findByNameAndUser(User user, String name) throws ServiceException {
		try {
			Application application = applicationDAO.findByNameAndUser(user.getId(), name, cuInstanceName);
			return application;
		} catch (PersistenceException e) {
			logger.error(user.toString(), e);
			throw new ServiceException(user.toString(), e);
		}
	}

	@Override
	@Transactional
	public Application deploy(MultipartFile file, Application application) throws ServiceException, CheckException {
		try {
			// get app with all its components
		    String filename = file.getOriginalFilename();
			String containerId = application.getServer().getContainerID();
			String tempDirectory = dockerService.getEnv(containerId, "CU_TMP");
			fileService.sendFileToContainer(containerId, tempDirectory, file, null, null);
			String contextPath = NamingUtils.getContext.apply(filename);
			@SuppressWarnings("serial")
            Map<String, String> kvStore = new HashMap<String, String>() {
				{
					put("CU_USER", application.getUser().getLogin());
					put("CU_PASSWORD", application.getUser().getPassword());
                    put("CU_FILE", filename);
					put("CU_CONTEXT_PATH", contextPath);
				}
			};
			String result = dockerService.execCommand(containerId, RemoteExecAction.DEPLOY.getCommand(kvStore));
			logger.info ("Deploy command {}", result);
			Deployment deployment = deploymentService.create(application, DeploymentType.from(filename), contextPath);
			application.addDeployment(deployment);
			application.setDeploymentStatus(Application.ALREADY_DEPLOYED);

			// If application is anything else than .jar or ROOT.war
			// we need to clean for the next deployment.
			if (!"/".equalsIgnoreCase(contextPath)) {
				@SuppressWarnings("serial")
				HashMap<String, String> kvStore2 = new HashMap<String, String>() {
					{
						put("CU_TARGET", Paths.get(tempDirectory, filename).toString());
					}
				};
				dockerService.execCommand(containerId, RemoteExecAction.CLEAN_DEPLOY.getCommand(kvStore2));
			}
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return application;
	}

	/**
	 * Return the list of containers for an application
	 *
	 * @param applicationName
	 * @return
	 * @throws ServiceException
	 */
	public List<ContainerUnit> listContainers(String applicationName) throws ServiceException {
		return listContainers(applicationName, true);
	}

	public List<ContainerUnit> listContainers(String applicationName, boolean withModules) throws ServiceException {
		List<ContainerUnit> containers = new ArrayList<>();
		try {
			User user = authentificationUtils.getAuthentificatedUser();
			Application application = findByNameAndUser(user, applicationName);
			if (application != null) {
				Server server = application.getServer();
				containers.add(new ContainerUnit(server.getName(), server.getContainerID(), "server"));
				if (withModules) {
					application.getModules().stream()
							.forEach(m -> containers.add(new ContainerUnit(m.getName(), m.getContainerID(), "module")));
				}
			}
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return containers;
	}

	@Override
	public boolean isStarted(String name) {
		int serversNotStarted = applicationDAO.countServersNotStatus(name, Status.START);
		int modulesNotStarted = applicationDAO.countModulesNotStatus(name, Status.START);
		logger.debug("serversNotStarted=" + serversNotStarted);
		logger.debug("modulesNotStarted=" + modulesNotStarted);
		return (serversNotStarted + modulesNotStarted) == 0;
	}

	@Override
	public boolean isStopped(String name) {
		int serversNotStopped = applicationDAO.countServersNotStatus(name, Status.STOP);
		int modulesNotStopped = applicationDAO.countModulesNotStatus(name, Status.STOP);
		logger.debug("serversNotStarted=" + serversNotStopped);
		logger.debug("modulesNotStarted=" + modulesNotStopped);
		return (serversNotStopped + modulesNotStopped) == 0;
	}
}

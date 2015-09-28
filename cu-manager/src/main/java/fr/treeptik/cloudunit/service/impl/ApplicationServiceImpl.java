/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.ContainerUnit;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.*;
import fr.treeptik.cloudunit.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class ApplicationServiceImpl implements ApplicationService {

	Locale locale = Locale.ENGLISH;

	private Logger logger = LoggerFactory
			.getLogger(ApplicationServiceImpl.class);

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
	private ShellUtils shellUtils;
	@Inject
	private HipacheRedisUtils hipacheRedisUtils;
	@Inject
	private AuthentificationUtils authentificationUtils;
	@Inject
	private PortUtils portUtils;
	@Inject
	private ContainerMapper containerMapper;
	@Inject
	private UserService userService;
	@Inject
	private MessageSource messageSource;

    @Value("${cloudunit.max.apps:100}")
    private String numberMaxApplications;

    @Value("${docker.manager.ip:192.168.50.4:4243}")
    private String dockerManagerIp;

    @Value("${suffix.cloudunit.io}")
    private String suffixCloudUnitIO;

	@Value("${java.version.default}")
	private String javaVersionDefault;

	@Value("${cloudunit.manager.ip}")
	private String restHost;

    public ApplicationDAO getApplicationDAO() {
		return this.applicationDAO;
	}

    /**
     * Test if the user can create new applications because we limit the number per user
     *
     * @param application
     * @param serverName
     * @throws CheckException
     * @throws ServiceException
     */
	@Override
	public void checkCreate(Application application, String serverName)
			throws CheckException, ServiceException {

        logger.debug("--CHECK APP COUNT--");

		if (this.countApp(application.getUser()) >= Integer.parseInt(numberMaxApplications)) {
			throw new ServiceException("You have already created your " + numberMaxApplications
					+ " apps into the Cloud");
		}

		try {
			if (this.checkAppExist(application.getUser(), application.getName())) {
				throw new CheckException(messageSource.getMessage("app.exists",
						null, locale));
			}
			if (imageService.findByName(serverName) == null)
				throw new CheckException(messageSource.getMessage(
						"image.not.found", null, locale));
			imageService.findByName(serverName);

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
	public boolean checkAppExist(User user, String applicationName)
			throws ServiceException, CheckException {
		logger.info("--CHECK APP EXIST--");
		if (applicationDAO.findByNameAndUser(user.getId(), applicationName) == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Save app in just in DB, not create container use principally to charge
	 * status.PENDING of entity until it's really functionnal
	 */
	@Override
	@Transactional
	public Application saveInDB(Application application)
			throws ServiceException {
		logger.debug("-- SAVE -- : " + application);
		// Do not affect application with save return.
		// You could lose the relationships.
		applicationDAO.save(application);
		return application;
	}

	/**
	 * Add git container to application associated to server
	 *
	 * @param application
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	private Module addGitContainer(Application application, String tagName)
			throws ServiceException, CheckException {

		Module moduleGit = ModuleFactory.getModule("git");
        // todo : externaliser la variable
		String containerGitAddress = "/cloudunit/git/.git";

		try {
			// Assign fixed host ports for forwarding git ports (22)
			Map<String, String> mapProxyPorts = portUtils
					.assignProxyPorts(application);
			String freeProxySshPortNumber = mapProxyPorts
					.get("freeProxySshPortNumber");

			// Creation of git container fo application
			moduleGit.setName("git");
			moduleGit.setImage(imageService.findByName("git"));
			moduleGit.setApplication(application);

			moduleGit.setSshPort(freeProxySshPortNumber);
			moduleGit = moduleService.initModule(application, moduleGit, tagName);

			application.getModules().add(moduleGit);
			application.setGitContainerIP(moduleGit.getContainerIP());

			application.setGitSshProxyPort(freeProxySshPortNumber);

			// Update GIT respository informations in the current application
			application.setGitAddress("ssh://"
					+ AlphaNumericsCharactersCheckUtils
					.convertToAlphaNumerics(application.getUser()
							.getLogin()) + "@" + application.getName()
					+ "." + application.getSuffixCloudUnitIO().substring(1)
					+ ":" + application.getGitSshProxyPort()
					+ containerGitAddress);

			moduleGit.setStatus(Status.START);
			moduleGit = moduleService.update(moduleGit);

		} catch (UnsupportedEncodingException e) {
			moduleGit.setStatus(Status.FAIL);
			logger.error("Error :  Error during persist git module " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return moduleGit;
	}

	/**
	 * Lancer par signal de NoPublicController quand le processus sshd est
	 * démarré dans les containers serveur et git
	 */
	public Application updateEnv(Application application, User user)
			throws ServiceException {

		logger.info("--update Env of Server--");
		String command = null;
		Map<String, String> configShellModule = new HashMap<>();
		Map<String, String> configShellServer = new HashMap<>();

		Module moduleGit = moduleService.findGitModule(user.getLogin(),	application);
		Server server = application.getServers().get(0);

		String rootPassword = application.getUser().getPassword();
		configShellModule.put("port", moduleGit.getSshPort());
		configShellModule.put("dockerManagerAddress", moduleGit.getApplication().getManagerIp());
		configShellModule.put("password", rootPassword);
		configShellModule.put("dockerManagerAddress", application.getManagerIp());
		logger.info("new server ip : " + server.getContainerIP());
		try {
			int counter = 0;
			while (!server.getStatus().equals(Status.START)
					|| !moduleGit.getStatus().equals(Status.START)) {
				if (counter == 100) { break; }
				Thread.sleep(1000);
				logger.info(" wait git and server sshd processus start");
				logger.info("SSHDSTATUS = server : " + server.getStatus() + " - module : " + moduleGit.getStatus());
				moduleGit = moduleService.findById(moduleGit.getId());
				server = serverService.findById(server.getId());
				counter++;
			}
			command = ". /cloudunit/scripts/update-env.sh "	+ server.getContainerIP();
			logger.info("command shell to execute [" + command + "]");

			shellUtils.executeShell(command, configShellModule);

			configShellServer.put("port", server.getSshPort());
			configShellServer.put("dockerManagerAddress", server.getApplication().getManagerIp());
			configShellServer.put("password", rootPassword);
			command = ". /cloudunit/scripts/rm-auth-keys.sh ";
			logger.info("command shell to execute [" + command + "]");

			shellUtils.executeShell(command, configShellServer);
			String cleanCommand = server.getServerAction().cleanCommand();
			if (cleanCommand != null) {
				shellUtils.executeShell(
						server.getServerAction().cleanCommand(),
						configShellServer);
			}
		} catch (Exception e) {
			moduleGit.setStatus(Status.FAIL);
			moduleGit = moduleService.saveInDB(moduleGit);
			server.setStatus(Status.FAIL);
			server = serverService.saveInDB(server);
			logger.error("Error :  Error during update Env var of GIT " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return application;
	}

	/**
	 * Lancer par signal de NoPublicController quand le processus sshd est
	 * (re)démarré dans container serveur et git, pour mettre à jour la nouvelle
	 * IP du serveur
	 */
	@Override
	public Application sshCopyIDToServer(Application application, User user)
			throws ServiceException {
		String command = null;
		Map<String, String> configShell = new HashMap<>();

		Module moduleGit = moduleService.findGitModule(user.getLogin(),
				application);

		if (logger.isDebugEnabled()) {
			logger.debug("--ssh Copy ID To Server--");
			logger.debug("ssh port : " + moduleGit.getSshPort());
			logger.debug("manager ip : " + application.getManagerIp());
		}

		for (Server server : application.getServers()) {
			configShell.put("password", server.getApplication().getUser().getPassword());
			configShell.put("port", moduleGit.getSshPort());
			configShell.put("dockerManagerAddress",	application.getManagerIp());
			configShell.put("userLogin", server.getApplication().getUser().getLogin());

			try {
				int counter = 0;
				while (!server.getStatus().equals(Status.START)
						|| !moduleGit.getStatus().equals(Status.START)) {
					if (counter == 100) {
						break;
					}
					Thread.sleep(1000);
					logger.info(" wait git and server ssh processus start");
					logger.info("STATUS = server : " + server.getStatus()
							+ " - module : " + moduleGit.getStatus());

					moduleGit = moduleService.findById(moduleGit.getId());
					server = serverService.findById(server.getId());
					counter++;
				}

				// To permit ssh access on server from git container
				command = "expect /cloudunit/scripts/ssh-copy-id-expect.sh "
						+ moduleGit.getApplication().getUser().getPassword();
				logger.info("command shell to execute [" + command + "]");

				shellUtils.executeShell(command, configShell);

			} catch (Exception e) {
				moduleGit.setStatus(Status.FAIL);
				moduleGit = moduleService.saveInDB(moduleGit);
				server.setStatus(Status.FAIL);
				server = serverService.saveInDB(server);
				logger.error("Error :  Error during permit git to access to server " + e);

				throw new ServiceException(e.getLocalizedMessage(), e);
			}
		}

		try {
			moduleGit = moduleService.update(moduleGit);

			application.getModules().add(moduleGit);
			application.setGitContainerIP(moduleGit.getContainerIP());

		} catch (ServiceException e) {
			moduleGit.setStatus(Status.FAIL);
			moduleService.saveInDB(moduleGit);
			logger.error("Error :  Error during persist git module " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		logger.info("ApplicationService : Application " + application.getName()
				+ " successfully created.");
		return application;
	}

	/**
	 * Methode qui teste la validité d'une application
	 *
	 * @param applicationName
	 * @param serverName
	 * @throws ServiceException
	 * @throws CheckException
	 */
	public void isValid(String applicationName, String serverName)
			throws ServiceException, CheckException {
		logger.info("--CALL APP IS VALID--");
		Application application = new Application();
		logger.info("applicationName = " + applicationName + ", serverName = "
				+ serverName);

		User user = authentificationUtils.getAuthentificatedUser();
		if (user == null) {
			throw new CheckException("User is not authentificated");
		}

		application.setName(applicationName);
		application.setUser(user);
		application.setModules(new ArrayList<>());

		this.checkCreate(application, serverName);
	}

	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public Application create(String applicationName, String login,
			String serverName, String tagName) throws ServiceException,
			CheckException {

		tagName = tagName == null ? null : ":" + tagName;
		if (applicationName != null) {
			applicationName = applicationName.toLowerCase();
		}

		logger.info("--CALL CREATE NEW APP--");
		Application application = new Application();

		logger.info("applicationName = " + applicationName + ", serverName = "
				+ serverName);

		User user = authentificationUtils.getAuthentificatedUser();

		// For cloning management
		if (tagName != null) {
			application.setAClone(true);
		}

		application.setName(applicationName);
        application.setUser(user);
        application.setModules(new ArrayList<>());

        // verify if application exists already
		this.checkCreate(application, serverName);

		// todo : use a session flag
		application.setStatus(Status.PENDING);

		application = this.saveInDB(application);
		serverService.checkMaxNumberReach(application);

		String subdomain = System.getenv("CU_SUB_DOMAIN") == null ? "" : System
				.getenv("CU_SUB_DOMAIN");

		List<Image> imagesEnabled = imageService.findEnabledImages();
		List<String> imageNames = new ArrayList<>();
		for (Image image : imagesEnabled) {
			imageNames.add(image.getName());
		}

		if (!imageNames.contains(serverName)) {
			throw new CheckException(messageSource.getMessage(
					"server.not.found", null, locale));
		}

		try {
			// BLOC APPLICATION
			application.setDomainName(subdomain	+ suffixCloudUnitIO);
			application = applicationDAO.save(application);
			application.setManagerIp(dockerManagerIp);
			application.setJvmRelease(javaVersionDefault);
			application.setRestHost(restHost);
			logger.info(application.getManagerIp());

			// BLOC SERVER
			Server server = ServerFactory.getServer(serverName);
			// We get image associated to server
			Image image = imageService.findByName(serverName);
			server.setImage(image);
			server.setApplication(application);
			server.setName(serverName);
			server = serverService.create(server, tagName);

			List<Server> servers = new ArrayList<>();
			servers.add(server);
			application.setServers(servers);

			// BLOC MODULE
			Module moduleGit = this.addGitContainer(application, tagName);
			application.getModules().add(moduleGit);
			application.setGitContainerIP(moduleGit.getContainerIP());

			// Persistence for Application model
			application = applicationDAO.save(application);

			// Copy the ssh key from the server to git container to be able to deploy war with gitpush
			// During clone processus, env variables are not updated. We must wait for a restart before
			// to copy the ssh keys for git push
			if (tagName == null) {
				this.sshCopyIDToServer(application, user);
			}

		} catch (DataAccessException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		logger.info("" + application);
		logger.info("ApplicationService : Application " + application.getName()
				+ " successfully created.");

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
	public Application remove(Application application, User user)
			throws ServiceException {

		try {
			logger.info("Starting removing application "
					+ application.getName());

            // Delete all modules
			List<Module> listModules = application.getModules();
			for (Module module : listModules) {
				try {
					moduleService.remove(application, user,
							module, false,
							application.getStatus());
				} catch (ServiceException | CheckException e) {
					application.setStatus(Status.FAIL);
					logger.error("ApplicationService Error : failed to remove module "
							+ module.getName()
							+ " for application "
							+ application.getName() + " : " + e);
					e.printStackTrace();
				}
			}

            // Delete all alias
			List<String> aliases = new ArrayList<>();
			aliases.addAll(application.getAliases());
			for (String alias : aliases) {
				removeAlias(application, alias);
			}

            // Delete all servers
			List<Server> listServers = application.getServers();
			for (Server server : listServers) {
				serverService.remove(server.getName());
				if (listServers.indexOf(server) == listServers.size() - 1) {
					hipacheRedisUtils.removeRedisAppKey(application);
					applicationDAO.delete(server.getApplication());
					portUtils.releaseProxyPorts(application);
				}
			}

			logger.info("ApplicationService : Application successfully removed ");

		} catch (PersistenceException e) {
			setStatus(application, Status.FAIL);
			logger.error("ApplicationService Error : failed to remove "
					+ application.getName() + " : " + e);

			throw new ServiceException(e.getLocalizedMessage(), e);
		} catch (ServiceException e) {
			setStatus(application, Status.FAIL);
			logger.error("ApplicationService Error : failed to remove application "
					+ application.getName() + " : " + e);
		}
		return application;
	}

	/**
	 * Methode permettant de mettre l'application dans un état particulier pour
	 * se prémunir d'éventuel problème de concurrence au niveau métier
	 */
	@Override
	@Transactional
	public void setStatus(Application application, Status status)
			throws ServiceException {
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
			User user = authentificationUtils.getAuthentificatedUser();
			logger.debug("start : Methods parameters : " + application);

			List<Module> modules = application.getModules();
			for (Module module : modules) {
				try {
					module = moduleService.startModule(module);
				} catch (ServiceException e) {
					logger.error("ApplicationService Error : failed to start "
							+ application.getName() + " : " + e);
					e.printStackTrace();
				}
			}
			List<Server> servers = application.getServers();
			for (Server server : servers) {
				logger.info("old server ip : " + server.getContainerIP());
				server = serverService.startServer(server);
			}

			if (application.getAliases() != null && !application.getAliases().isEmpty()) {
				updateAliases(application);
			}
			logger.info("ApplicationService : Application successfully started ");
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return application;
	}

	@Override
	public Application postStart(Application application, User user)
			throws ServiceException {
		application = this.updateEnv(application, user);
		application = this.sshCopyIDToServer(application, user);
		return application;
	}

	@Override
	@Transactional
	public Application stop(Application application) throws ServiceException {

		try {
			List<Server> servers = application.getServers();
			for (Server server : servers) {
				server = serverService.stopServer(server);
			}
			List<Module> modules = application.getModules();
			for (Module module : modules) {
				try {
					module = moduleService.stopModule(module);
				} catch (ServiceException e) {
					logger.error("ApplicationService Error : failed to stop "
							+ application.getName() + " : " + e);
				}
			}
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
				application.setServers(serverService.findByApp(application));
				application.setModules(moduleService.findByAppAndUser(
						application.getUser(), application.getName()));
			}
			logger.debug("ApplicationService : All Applications found ");
			return listApplications;
		} catch (PersistenceException e) {
			logger.error("Error ApplicationService : error findAll Method : "
					+ e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Application> findAllByUser(User user) throws ServiceException {
		try {
			List<Application> applications = applicationDAO.findAllByUser(user.getId());
			logger.debug("ApplicationService : All Applications found ");
			return applications;
		} catch (PersistenceException e) {
			logger.error("Error ApplicationService : error findById Method : "
					+ user);
			throw new ServiceException(user.toString(), e);
		}
	}

	@Override
	public Application findByNameAndUser(User user, String name)
			throws ServiceException {
		try {
			Application application = applicationDAO.findByNameAndUser(
					user.getId(), name);

			return application;

		} catch (PersistenceException e) {
			logger.error(user.toString(), e);
			throw new ServiceException(user.toString(), e);
		}
	}

	@Transactional
	public void deployToContainerId(String applicationName, String containerId,
			File file, String destFile) throws ServiceException {

		try {
			Application application = this.findByNameAndUser(
                    authentificationUtils.getAuthentificatedUser(),
                    applicationName);

			Map<String, String> configShell = new HashMap<>();

			String sshPort = application.getSShPortByContainerId(containerId);
			String rootPassword = application.getUser().getPassword();
			configShell.put("port", sshPort);
			configShell.put("dockerManagerAddress",
					application.getManagerIp());
			configShell.put("password", rootPassword);

			// send the file on container
			shellUtils.sendFile(file, rootPassword, sshPort,
					application.getManagerIp(), destFile);

        } catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional
	public Application deploy(File file, Application application)
			throws ServiceException, CheckException {

		Integer code = null;
		Map<String, String> configShell = new HashMap<>();

		try {
			// get app with all its components

			for (Server server : application.getServers()) {

				// loading server ssh informations

				String rootPassword = server.getApplication().getUser()
						.getPassword();
				configShell.put("port", server.getSshPort());
				configShell.put("dockerManagerAddress",
						application.getManagerIp());
				configShell.put("password", rootPassword);
				String destFile = "/cloudunit/tmp/";

				// send the file on container

				shellUtils.sendFile(file, rootPassword, server.getSshPort(),
						application.getManagerIp(), destFile);

				// call deployment script

				code = shellUtils.executeShell(
						"bash /cloudunit/scripts/deploy.sh " + file.getName()
								+ " " + application.getUser().getLogin(),
						configShell);

			}

			// if all is ok, create a new deployment tag and set app to starting

			if (code == 0) {
				deploymentService.create(application, Type.WAR);
			} else {
				throw new CheckException("No way to deploy application " + file + ", " + application);
			}

		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

		return application;
	}

	@Override
	@Transactional
	public Application saveGitPush(Application application, String login)
			throws ServiceException, CheckException {
		logger.info("parameters - application : " + application.toString());
		deploymentService.create(application, Type.GITPUSH);
		return application;
	}

	@Override
	public Long countApp(User user) throws ServiceException {
		try {
			return applicationDAO.countApp(user.getId());
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	/** Liste des containers pour une application
	 *
	 * @param applicationName
	 * @return
	 * @throws ServiceException
	 */
	public List<ContainerUnit> listContainers(String applicationName)
			throws ServiceException {
		return listContainers(applicationName, true);
	}

	public List<ContainerUnit> listContainers(String applicationName,
			boolean withModules) throws ServiceException {
		List<ContainerUnit> containers = new ArrayList<>();
		try {
			Application application = findByNameAndUser(
					authentificationUtils.getAuthentificatedUser(),
					applicationName);
			if (application != null) {
				try {
					// Serveurs
					List<Server> servers = application.getServers();
					// Ajout des containers de type server
					for (Server server : servers) {
						DockerContainer dockerContainer = new DockerContainer();
						dockerContainer.setName(server.getName());
						dockerContainer = DockerContainer.findOne(
								dockerContainer, application.getManagerIp());
						server = containerMapper.mapDockerContainerToServer(
								dockerContainer, server);
						ContainerUnit containerUnit = new ContainerUnit(
								server.getName(), server.getContainerID(),
								"server");
						containers.add(containerUnit);
					}
					if (withModules) {
						// Ajout des containers de type module
						List<Module> modules = application.getModules();
						for (Module module : modules) {
							// on evite de remonter les modules de type toolkit
							// (git, maven...)
							if (module.isTool()) {
								continue;
							}
							DockerContainer dockerContainer = new DockerContainer();
							dockerContainer.setName(module.getName());
							dockerContainer = DockerContainer.findOne(
									dockerContainer,
									application.getManagerIp());
							module = containerMapper
									.mapDockerContainerToModule(
											dockerContainer, module);
							ContainerUnit containerUnit = new ContainerUnit(
									module.getName(), module.getContainerID(),
									"module");
							containers.add(containerUnit);
						}
					}
				} catch (Exception ex) {
					// Si une application sort en erreur, il ne faut pas
					// arrêter la suite des traitements
					logger.error(application.toString(), ex);
				}
			}

		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return containers;
	}

	/**
	 * Liste des containers pour une application
	 *
	 * @param applicationName
	 * @return
	 * @throws ServiceException
	 */
	public List<String> listContainersId(String applicationName)
			throws ServiceException {
		return listContainersId(applicationName, false);
	}

	public List<String> listContainersId(String applicationName,
			boolean withModules) throws ServiceException {
		List<String> containers = new ArrayList<>();
		try {
			Application application = findByNameAndUser(
					authentificationUtils.getAuthentificatedUser(),
					applicationName);
			if (application != null) {
				try {
					// Serveurs
					List<Server> servers = application.getServers();
					// Ajout des containers de type server
					for (Server server : servers) {
						DockerContainer dockerContainer = new DockerContainer();
						dockerContainer.setName(server.getName());
						dockerContainer = DockerContainer.findOne(
								dockerContainer, application.getManagerIp());
						server = containerMapper.mapDockerContainerToServer(
								dockerContainer, server);
						containers.add(server.getContainerID());
					}
					// Ajout des containers de type module
					if (withModules) {
						List<Module> modules = application.getModules();
						for (Module module : modules) {
							DockerContainer dockerContainer = new DockerContainer();
							dockerContainer.setName(module.getName());
							dockerContainer = DockerContainer.findOne(
									dockerContainer,
									application.getManagerIp());
							module = containerMapper
									.mapDockerContainerToModule(
											dockerContainer, module);
							containers.add(module.getContainerID());
						}
					}
				} catch (Exception ex) {
					// Si une application sort en erreur, il ne faut pas
					// arrêter la suite des traitements
					logger.error(application.toString(), ex);
				}
			}

		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return containers;
	}

	@Override
	public List<String> getListAliases(Application application)
			throws ServiceException {
		try {
			return applicationDAO.findAllAliases(application.getName());
		} catch (DataAccessException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional
	public void addNewAlias(Application application, String alias)
			throws ServiceException, CheckException {

		logger.info("ALIAS VALUE IN addNewAlias : " + alias);

		if (checkAliasIfExists(alias)) {
			throw new CheckException(
					"This alias is already used by another application on this CloudUnit instance");
		}

		if (alias.startsWith("https://") || alias.startsWith("http://")
				|| alias.startsWith("ftp://")) {
			alias = alias
					.substring(alias.lastIndexOf("//") + 2, alias.length());
		}

		if (alias.contains("/")) {
			throw new CheckException(
					"This alias contains forbidden characters \"/\". Please remove them");
		}

		try {
			Server server = application.getServers().get(0);
			application.getAliases().add(alias);
			hipacheRedisUtils.writeNewAlias(alias, application, server
					.getServerAction().getServerPort());
            applicationDAO.save(application);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
    @Transactional
	public void updateAliases(Application application) throws ServiceException {
		try {
			Server server = application.getServers().get(0);
			List<String> aliases = applicationDAO.findAllAliases(application
					.getName());
			for (String alias : aliases) {
				hipacheRedisUtils.updateAlias(alias, application,
						server.getServerAction().getServerPort());
			}

		} catch (DataAccessException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional
	public void removeAlias(Application application, String alias)
			throws ServiceException {
		try {
			hipacheRedisUtils.removeAlias(alias);
			application.getAliases().remove(alias);
			application = applicationDAO.save(application);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	private boolean checkAliasIfExists(String alias) {
		if (applicationDAO.findAliasesForAllApps().contains(alias)) {
			return true;
		}
		return false;
	}

}

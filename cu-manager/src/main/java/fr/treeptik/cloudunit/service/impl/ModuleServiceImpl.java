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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ModuleStartEvent;
import fr.treeptik.cloudunit.config.events.ModuleStopEvent;
import fr.treeptik.cloudunit.dao.ModuleDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.service.ImageService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.AlphaNumericsCharactersCheckUtils;
import fr.treeptik.cloudunit.utils.EmailUtils;
import fr.treeptik.cloudunit.utils.ModuleUtils;

@Service
public class ModuleServiceImpl implements ModuleService {

	private Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

	@Inject
	private ModuleDAO moduleDAO;

	@Inject
	private EnvironmentService environmentService;

	@Inject
	private ImageService imageService;

	@Inject
	private ApplicationService applicationService;

	@Inject
	private UserService userService;

	@Inject
	private EmailUtils emailUtils;

	@Inject
	private DockerService dockerService;

	@Value("${suffix.cloudunit.io}")
	private String suffixCloudUnitIO;

	@Value("${database.password}")
	private String databasePassword;

	@Value("${env.exec}")
	private String envExec;

	@Value("${cloudunit.instance.name}")
	private String cuInstanceName;

	@Value("${database.hostname}")
	private String databaseHostname;

	@Value("${certs.dir.path}")
	private String certsDirPath;

	@Value("${docker.endpoint.mode}")
	private String dockerEndpointMode;

	@Value("${docker.manager.ip:192.168.50.4:2376}")
	private String dockerManagerIp;

	@Inject
	private ApplicationEventPublisher applicationEventPublisher;

	private boolean isHttpMode;

	@PostConstruct
	public void initDockerEndPointMode() {
		if ("http".equalsIgnoreCase(dockerEndpointMode)) {
			logger.warn("Docker TLS mode is disabled");
			setHttpMode(true);
		} else {
			setHttpMode(false);
		}
	}

	@Override
	@Transactional
	public Module create(String imageName, String applicationName, User user) throws ServiceException, CheckException {
		Application application = applicationService.findByNameAndUser(user, applicationName);
		applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
		// General informations
		checkImageExist(imageName);
		Module module = new Module();
		module.setImage(imageService.findByName(imageName));
		module.setName(imageName);
		module.setApplication(application);
		module.setStatus(Status.PENDING);
		module.setStartDate(new Date());
		module.setPublishPorts(false);

		// Build a custom container
		String containerName = "";
		try {
			containerName = AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(cuInstanceName.toLowerCase()) + "-"
					+ AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(user.getLogin()) + "-"
					+ AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(module.getApplication().getName()) + "-"
					+ module.getName();
		} catch (UnsupportedEncodingException e2) {
			throw new ServiceException("Error rename Server", e2);
		}
		String imagePath = module.getImage().getPath();
		logger.debug("imagePath:" + imagePath);

		String subdomain = System.getenv("CU_SUB_DOMAIN");
		if (subdomain == null) {
			subdomain = "";
		}
		logger.info("env.CU_SUB_DOMAIN=" + subdomain);

		module.setInternalDNSName(containerName + "." + imageName + ".cloud.unit");
		module.getApplication().setSuffixCloudUnitIO(subdomain + suffixCloudUnitIO);

		try {

			Map<String, String> moduleUserAccess = ModuleUtils.generateRamdomUserAccess();
			moduleUserAccess.put("database", applicationName);

			List<String> envs = Arrays.asList("POSTGRES_PASSWORD=" + moduleUserAccess.get("password"),
					"POSTGRES_USER=" + moduleUserAccess.get("username"), "POSTGRES_DB=" + applicationName);
			module.setModuleInfos(moduleUserAccess);

			List<EnvironmentVariable> environmentVariables = new ArrayList<>();
			EnvironmentVariable environmentVariable = new EnvironmentVariable();
			environmentVariable.setKeyEnv("CU_DATABASE_USER_POSTGRESQL_1");
			environmentVariable.setValueEnv(moduleUserAccess.get("username"));
			environmentVariables.add(environmentVariable);
			environmentVariable = new EnvironmentVariable();
			environmentVariable.setKeyEnv("CU_DATABASE_PASSWORD_POSTGRESQL_1");
			environmentVariable.setValueEnv(moduleUserAccess.get("password"));
			environmentVariables.add(environmentVariable);
			environmentVariable = new EnvironmentVariable();
			environmentVariable.setKeyEnv("CU_DATABASE_NAME");
			environmentVariable.setValueEnv(applicationName);
			environmentVariables.add(environmentVariable);
			environmentVariable = new EnvironmentVariable();
			environmentVariable.setKeyEnv("CU_DATABASE_DNS_POSTGRESQL_1");
			environmentVariable.setValueEnv(module.getInternalDNSName());
			environmentVariables.add(environmentVariable);
			environmentService.save(user, environmentVariables, applicationName, application.getServer().getName());
			dockerService.createModule(containerName, module, imagePath, user, envs, true, new ArrayList<>());
			module = dockerService.startModule(containerName, module);
			module = moduleDAO.save(module);
			applicationEventPublisher.publishEvent(new ModuleStartEvent(module));
			applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
		} catch (PersistenceException e) {
			logger.error("ServerService Error : Create Server " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		} catch (DockerJSONException e) {
			StringBuilder msgError = new StringBuilder(512);
			msgError.append("server=").append(module);
			logger.error("" + msgError, e);
			throw new ServiceException(msgError.toString(), e);
		}
		return module;
	}

	/**
	 * Save app in just in DB, not create container use principally to charge
	 * status.PENDING of entity until it's really functionnal
	 */
	@Override
	@Transactional(rollbackFor = ServiceException.class)
	public Module publishPort(Integer id, Boolean publishPort, String applicationName, User user)
			throws ServiceException {
		Application application = applicationService.findByNameAndUser(user, applicationName);
		Module module = findById(id);
		applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
		applicationEventPublisher.publishEvent(new ModuleStopEvent(module));
		module = moduleDAO.saveAndFlush(module);
		List<String> envs = environmentService.loadEnvironnmentsByContainer(module.getName()).stream()
				.map(e -> e.getKeyEnv() + "=" + e.getValueEnv()).collect(Collectors.toList());
		dockerService.removeContainer(module.getName(), false);
		dockerService.createModule(module.getName(), module, module.getImage().getPath(), user, envs, false,
				new ArrayList<String>());
		applicationEventPublisher.publishEvent(new ModuleStartEvent(module));
		applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
		return module;
	}

	@Transactional(rollbackFor = ServiceException.class)

	private void sendEmail(Module module) throws ServiceException {

		Map<String, Object> mapConfigEmail = new HashMap<>();

		mapConfigEmail.put("module", module);
		mapConfigEmail.put("user", userService.findById(module.getApplication().getUser().getId()));
		mapConfigEmail.put("emailType", "moduleInformations");

		try {
			if ("apache".equalsIgnoreCase(module.getName()) == false) {
				emailUtils.sendEmail(mapConfigEmail);
			}
		} catch (MessagingException e) {
			logger.error("Error while sending email " + e);
			// On ne bloque pas l'appli pour une erreur d'email
			// Les infos sont aussi dans le CLI
		}
	}

	public void checkImageExist(String moduleName) throws ServiceException {
		Image image = imageService.findByName(moduleName);
		if (image == null) {
			throw new ServiceException("Error : the module " + moduleName + " is not available");
		}

	}

	@Override
	@Transactional
	public Module update(Module module) throws ServiceException {
		logger.debug("update : Methods parameters : " + module.toString());
		logger.info("ModuleService : Starting updating Module " + module.getName());
		try {
			module = moduleDAO.save(module);
		} catch (PersistenceException e) {
			module.setStatus(Status.FAIL);
			module = moduleDAO.save(module);
			logger.error("ModuleService Error : update Module" + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		logger.info("ModuleService : Module " + module.getName() + " successfully updated.");
		return module;
	}

	@Override
	@Transactional
	public void remove(User user, String moduleName, Boolean isModuleRemoving, Status previousApplicationStatus)
			throws ServiceException, CheckException {

		try {
			Module module = this.findByName(moduleName);

			dockerService.removeContainer(module.getName(), true);
			moduleDAO.delete(module);

			logger.info("Module successfully removed ");
		} catch (PersistenceException e) {
			logger.error("Error database :  " + moduleName + " : " + e);
			throw new ServiceException("Error database :  " + e.getLocalizedMessage(), e);
		} catch (DockerJSONException e) {
			logger.error(moduleName, e);
		}
	}

	@Override
	@Transactional
	public Module startModule(String moduleName) throws ServiceException {
		logger.info("Module : Starting module " + moduleName);
		Module module = null;

		try {
			module = findByName(moduleName);
			module = dockerService.startModule(moduleName, module);
			applicationEventPublisher.publishEvent(new ModuleStartEvent(module));

		} catch (PersistenceException e) {
			logger.error("ModuleService Error : fail to start Module" + moduleName);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return module;
	}

	@Override
	@Transactional
	public Module stopModule(String moduleName) throws ServiceException {
		Module module = null;
		try {
			module = findByName(moduleName);
			dockerService.stopContainer(moduleName);
			applicationEventPublisher.publishEvent(new ModuleStopEvent(module));
		} catch (DataAccessException e) {
			logger.error("[" + moduleName + "] Fail to stop Module : " + moduleName);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return module;
	}

	@Override
	public Module findById(Integer id) throws ServiceException {
		try {
			logger.debug("findById : Methods parameters : " + id);
			Module module = moduleDAO.findOne(id);
			logger.info("Module with id " + id + " found!");
			return module;
		} catch (PersistenceException e) {
			logger.error("Error ModuleService : error findById Method : " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Module> findAll() throws ServiceException {
		try {
			logger.debug("start findAll");
			List<Module> modules = moduleDAO.findAll();
			logger.info("ModuleService : All Modules found ");
			return modules;
		} catch (PersistenceException e) {
			logger.error("Error ModuleService : error findAll Method : " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Module> findAllStatusStartModules() throws ServiceException {
		List<Module> listModules = this.findAll();
		List<Module> listStatusStartModules = new ArrayList<>();

		for (Module module : listModules) {
			if (Status.START == module.getStatus()) {
				listStatusStartModules.add(module);
			}
		}
		return listStatusStartModules;
	}

	@Override
	public List<Module> findAllStatusStopModules() throws ServiceException {
		List<Module> listModules = this.findAll();
		List<Module> listStatusStopModules = new ArrayList<>();

		for (Module module : listModules) {
			if (Status.STOP == module.getStatus()) {
				listStatusStopModules.add(module);
			}
		}
		return listStatusStopModules;
	}

	@Override
	public Module findByContainerID(String id) throws ServiceException {
		try {
			return moduleDAO.findByContainerID(id);
		} catch (PersistenceException e) {
			logger.error("Error ModuleService : error findCloudId Method : " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Module findByName(String moduleName) throws ServiceException {
		try {
			logger.debug("findByName : " + moduleName);
			Module module = moduleDAO.findByName(moduleName);
			logger.debug("findByName : " + module);
			return module;
		} catch (PersistenceException e) {
			logger.error("Error ModuleService : error findName Method : " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Module> findByApp(Application application) throws ServiceException {
		try {
			return moduleDAO.findByApp(application.getName(), cuInstanceName);
		} catch (PersistenceException e) {
			logger.error("Error ModuleService : error findByApp Method : " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Module> findByAppAndUser(User user, String applicationName) throws ServiceException {
		try {
			List<Module> modules = moduleDAO.findByAppAndUser(user.getId(), applicationName, cuInstanceName);
			return modules;
		} catch (PersistenceException e) {
			logger.error("Error ModuleService : error findByAppAndUser Method : " + e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	public boolean isHttpMode() {
		return isHttpMode;
	}

	public void setHttpMode(boolean isHttpMode) {
		this.isHttpMode = isHttpMode;
	}

}

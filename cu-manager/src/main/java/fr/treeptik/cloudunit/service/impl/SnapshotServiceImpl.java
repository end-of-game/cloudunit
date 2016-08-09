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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.dao.ModuleConfigurationDAO;
import fr.treeptik.cloudunit.dao.SnapshotDAO;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.ModuleConfiguration;
import fr.treeptik.cloudunit.model.ModuleFactory;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Snapshot;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.HookService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.service.SnapshotService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Service
public class SnapshotServiceImpl implements SnapshotService {

	private Logger logger = LoggerFactory.getLogger(SnapshotServiceImpl.class);

	@Inject
	private ApplicationService applicationService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private SnapshotDAO snapshotDAO;

	@Inject
	private ModuleConfigurationDAO moduleConfigurationDAO;

	@Inject
	private ModuleService moduleService;

	@Inject
	private HookService hookService;

	@Inject
	private ServerService serverService;

	@Value("${cloudunit.max.apps:100}")
	private String numberMaxApplications;

	@Value("${docker.manager.ip:192.168.50.4:2376}")
	private String dockerManagerIp;

	@Value("${cloudunit.instance.name}")
	private String cuInstanceName;

	@Value("${certs.dir.path}")
	private String certsDirPath;

	@Value("${docker.endpoint.mode}")
	private String dockerEndpointMode;

	private boolean isHttpMode;

	@PostConstruct
	public void initDockerEndPointMode() {
		if ("http".equalsIgnoreCase(dockerEndpointMode)) {
			logger.warn("Docker TLS mode is disabled");
			isHttpMode = true;
		} else {
			isHttpMode = false;
		}
	}

	@Override
	public Snapshot findOne(String tag) {
		return snapshotDAO.findByTag(tag);
	}

	@Override
	@Transactional
	public Snapshot create(String applicationName, User user, String tag, String description, Status previousStatus)
			throws ServiceException, CheckException {

		Snapshot snapshot = new Snapshot();
		ObjectMapper objectMapper = new ObjectMapper();

		Application application = applicationService.findByNameAndUser(user, applicationName);

		String testTag = tag.toLowerCase();
		testTag = Normalizer.normalize(testTag, Normalizer.Form.NFD);
		testTag = testTag.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		testTag = testTag.replaceAll("[^a-z0-9]", "");

		if (testTag.length() == 0) {
			applicationService.setStatus(application, previousStatus);
			authentificationUtils.allowUser(user);
			throw new CheckException("This tag has a length equal to zero : " + tag);
		}

		if (tag != null) {
			tag = tag.toLowerCase();
		}

		if (tagExists(tag, user.getLogin())) {
			applicationService.setStatus(application, previousStatus);
			authentificationUtils.allowUser(user);
			throw new CheckException("this tag already exists for this user : " + tag + ", " + user.getLogin());
		}

		if (tag.equalsIgnoreCase("") || tag == null || tag.equalsIgnoreCase(" ")) {
			applicationService.setStatus(application, previousStatus);
			authentificationUtils.allowUser(user);
			throw new CheckException("You must put a tag name");
		}

		try {
			snapshot.setApplicationName(application.getName());
			snapshot.setApplicationDisplayName(application.getDisplayName());
			snapshot.setDate(new Date());
			snapshot.setTag(tag);
			snapshot.setDisplayTag(tag);
			snapshot.setFullTag(user.getLogin() + "-" + tag);
			snapshot.setCuInstanceName(cuInstanceName);
			snapshot.setDescription(description);
			snapshot.setUser(application.getUser());
			snapshot.setDeploymentStatus(application.getDeploymentStatus());

			snapshot.setSavedPorts(application.getPortsToOpen().stream().map(c -> c.getPort() + ";" + c.getNature())
					.collect(Collectors.toList()));

			Map<String, ModuleConfiguration> config = new HashMap<>();
			Server server = application.getServer();
			// snapshot = server.getServerAction().cloneProperties(snapshot);

			for (Module module : application.getModules()) {
					ModuleConfiguration moduleConfiguration = moduleConfigurationDAO
							.saveAndFlush(module.getModuleAction().cloneProperties());
					config.put(moduleConfiguration.getPath(), moduleConfiguration);
			}

			snapshot.setAppConfig(config);

			snapshot = snapshotDAO.save(snapshot);

		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return snapshot;
	}

	public Snapshot findByTagAndUser(String login, String tag) {
		if (tag != null) {
			tag = tag.toLowerCase();
		}
		return snapshotDAO.findByTag(login + "-" + tag);
	}

	@Override
	public List<Snapshot> listAll() throws ServiceException {
		try {
			return snapshotDAO.listAll();
		} catch (DataAccessException e) {
			throw new ServiceException("Error : " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional
	public Snapshot remove(String tag) throws ServiceException, CheckException {
		Snapshot snapshot = null;
		if (tag != null) {
			tag = tag.toLowerCase();
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();

			snapshot = snapshotDAO.findByTag(tag);

			if (snapshot == null) {
				throw new CheckException("Error : this snapshot doesn't exist : " + tag);
			}

			List<String> images = snapshotDAO.findAllImagesFromASnapshot(tag).getImages();

			for (String image : images) {
				// DockerContainer dockerContainer = new DockerContainer();
				// dockerContainer.setImage(image);
				// DockerContainer.deleteImage(image + ":" + tag,
				// dockerManagerIp);
			}

			snapshotDAO.delete(snapshotDAO.findByTag(tag));

		} catch (DataAccessException e) {
			throw new ServiceException("Error : " + e.getLocalizedMessage(), e);
		}
		// we return for aop compliant. todo : change it
		return snapshot;
	}

	@Override
	@Transactional
	public Snapshot cloneFromASnapshot(String applicationName, String tag)
			throws ServiceException, InterruptedException, CheckException {

		Snapshot snapshot = null;
		// Tests préliminaires de la création d'une application
		try {
			User user = authentificationUtils.getAuthentificatedUser();
			snapshot = this.findByTagAndUser(user.getLogin(), tag);

			if (applicationName == null | applicationName.equalsIgnoreCase("")) {
				authentificationUtils.allowUser(user);
				throw new CheckException("Please put an app name");
			}

			if (applicationService.checkAppExist(user, applicationName)) {
				authentificationUtils.allowUser(user);
				throw new CheckException("This application already exists");
			}

			if (!tagExists(tag, user.getLogin())) {
				throw new CheckException("This tag does not exist yet");
			}

			// creation de la nouvelle app à partir de l'image tagée
			Application application = applicationService.create(applicationName, user.getLogin(), snapshot.getType(),
					snapshot.getFullTag(), snapshot.getTag());

			// We need it to get lazy modules relationships
			application = applicationService.findByNameAndUser(application.getUser(), application.getName());

			Server server = application.getServer();
			serverService.update(server, snapshot.getJvmMemory().toString(), snapshot.getJvmOptions(),
						snapshot.getJvmRelease(), false);

			restoreModules(snapshot, application, tag);

			application.setDeploymentStatus(snapshot.getDeploymentStatus());
			applicationService.saveInDB(application);

			for (String savedPort : snapshot.getSavedPorts()) {
				applicationService.addPort(application, savedPort.split(";")[1],
						Integer.parseInt(savedPort.split(";")[0]));
			}

		} catch (ServiceException e) {
			StringBuilder msgError = new StringBuilder(1024);
			msgError.append("applicationName=[").append(applicationName).append("]");
			msgError.append(", snapshot=[").append(snapshot).append("]");
			msgError.append(", tag=[").append(tag).append("]");
			throw new ServiceException(msgError.toString(), e);
		}
		return snapshot;
	}

	private void backupModule(Module module) {
		/*
		 * DockerClient docker = null; try { if (Boolean.valueOf(isHttpMode)) {
		 * docker = DefaultDockerClient .builder() .uri("http://" +
		 * dockerManagerIp).build(); } else { final DockerCertificates certs =
		 * new DockerCertificates(Paths.get(certsDirPath)); docker =
		 * DefaultDockerClient .builder() .uri("https://" +
		 * dockerManagerIp).dockerCertificates(certs).build(); }
		 * 
		 * final String[] commandStop = {"bash", "-c",
		 * "/cloudunit/scripts/cu-stop.sh"}; final String[] commandBackupData =
		 * {"bash", "-c", "/cloudunit/scripts/backup-data.sh"}; final String[]
		 * commandStart = {"bash", "-c", "/cloudunit/scripts/cu-start.sh"};
		 * 
		 * String execId = docker.execCreate(module.getName(), commandStop,
		 * DockerClient.ExecParameter.STDOUT,
		 * DockerClient.ExecParameter.STDERR); LogStream output =
		 * docker.execStart(execId); String execOutput = output.readFully();
		 * logger.debug(execOutput); if (output != null) { output.close(); }
		 * 
		 * execId = docker.execCreate(module.getName(), commandBackupData,
		 * DockerClient.ExecParameter.STDOUT,
		 * DockerClient.ExecParameter.STDERR); output =
		 * docker.execStart(execId); execOutput = output.readFully();
		 * logger.debug(execOutput); if (output != null) { output.close(); }
		 * 
		 * execId = docker.execCreate(module.getName(), commandStart,
		 * DockerClient.ExecParameter.STDOUT,
		 * DockerClient.ExecParameter.STDERR); output =
		 * docker.execStart(execId); execOutput = output.readFully();
		 * logger.debug(execOutput); if (output != null) { output.close(); }
		 * 
		 * } catch (Exception e) { logger.error(e.getMessage() + ", " + module);
		 * } finally { if (docker != null) { docker.close(); } }
		 */
	}

	/**
	 * Restore all modules
	 *
	 * @param snapshot
	 * @param application
	 * @param tag
	 * @throws ServiceException
	 */
	private void restoreModules(Snapshot snapshot, Application application, String tag) throws ServiceException {

		for (String key : snapshot.getAppConfig().keySet()) {
			try {
				hookService.call(snapshot.getAppConfig().get(key).getName(), RemoteExecAction.CLONE_PRE_ACTION);
				Module module = ModuleFactory.getModule(snapshot.getAppConfig().get(key).getName());
				module.setApplication(application);
				moduleService.checkImageExist(snapshot.getAppConfig().get(key).getName());
				module.getImage().setName(snapshot.getAppConfig().get(key).getName());
				module.setName(snapshot.getAppConfig().get(key).getName());
				module = moduleService.initModule(application, module, snapshot.getFullTag());

				Map<String, String> properties = new HashMap<>();
				properties.put("username", snapshot.getAppConfig().get(key).getProperties()
						.get("username-" + module.getImage().getName()));
				properties.put("password", snapshot.getAppConfig().get(key).getProperties()
						.get("password-" + module.getImage().getName()));
				properties.put("database", snapshot.getAppConfig().get(key).getProperties()
						.get("database-" + module.getImage().getName()));
				module.setModuleInfos(properties);
				module = moduleService.saveInDB(module);

				if (tag != null) {
					restoreDataModule(module);
				}
				hookService.call(snapshot.getAppConfig().get(key).getName(), RemoteExecAction.CLONE_POST_ACTION);
			} catch (CheckException e) {
				throw new ServiceException(e.getLocalizedMessage(), e);
			}
		}
	}

	private void restoreDataModule(Module module) {
		/*
		 * DockerClient docker = null; try { if (Boolean.valueOf(isHttpMode)) {
		 * docker = DefaultDockerClient .builder() .uri("http://" +
		 * dockerManagerIp).build(); } else { final DockerCertificates certs =
		 * new DockerCertificates(Paths.get(certsDirPath)); docker =
		 * DefaultDockerClient .builder() .uri("https://" +
		 * dockerManagerIp).dockerCertificates(certs).build(); }
		 * 
		 * final String[] commandRestoreData = {"bash", "-c",
		 * "/cloudunit/scripts/restore-data.sh"}; String execId =
		 * docker.execCreate(module.getName(), commandRestoreData,
		 * DockerClient.ExecParameter.STDOUT,
		 * DockerClient.ExecParameter.STDERR); LogStream output =
		 * docker.execStart(execId); String execOutput = output.readFully();
		 * logger.debug(execOutput); if (output != null) { output.close(); }
		 * 
		 * } catch (Exception e) { logger.error(e.getMessage() + ", " + module);
		 * } finally { if (docker != null) { docker.close(); } }
		 */
	}

	private boolean tagExists(String tag, String login) {
		if (snapshotDAO.findByTag(login + "-" + tag) != null) {
			return true;
		}
		return false;
	}
}

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

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.treeptik.cloudunit.dao.ModuleConfigurationDAO;
import fr.treeptik.cloudunit.dao.SnapshotDAO;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.*;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.ShellUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@Service
public class SnapshotServiceImpl implements SnapshotService {

	private Logger logger = LoggerFactory.getLogger(SnapshotServiceImpl.class);

	@Inject
	private Environment env;

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
	private ImageService imageService;

	@Inject
	private ShellUtils shellUtils;

	@Inject
	private ServerService serverService;

	@Override
	@Transactional
	public Snapshot create(String applicationName, User user, String tag,
			String description, Status previousStatus) throws ServiceException {

		Snapshot snapshot = new Snapshot();
		ObjectMapper objectMapper = new ObjectMapper();
		try {

			Application application = applicationService.findByNameAndUser(
					user, applicationName);

			if (this.tagExists(tag, user.getLogin())) {
				applicationService.setStatus(application, previousStatus);
				authentificationUtils.allowUser(user);
				throw new CheckException("this tag already exists");
			}

			if (tag.equalsIgnoreCase("") || tag == null
					|| tag.equalsIgnoreCase(" ")) {
				applicationService.setStatus(application, previousStatus);
				authentificationUtils.allowUser(user);
				throw new CheckException("You must put a tag name");
			}

			snapshot.setApplicationName(application.getName());
			snapshot.setDate(new Date());
			snapshot.setTag(tag);
			snapshot.setDescription(description);
			snapshot.setUser(application.getUser());
			snapshot.setDeploymentStatus(application.getDeploymentStatus());

			/**
			 * Pour se protéger des appels irraisonnés du webui
			 */

			Map<String, ModuleConfiguration> config = new HashMap<>();
			for (Server server : application.getServers()) {
				snapshot = server.getServerAction().cloneProperties(snapshot);
			}
			for (Module module : application.getModules()) {

				if (!module.getImage().getPath().contains("git")) {

					ModuleConfiguration moduleConfiguration = moduleConfigurationDAO
							.saveAndFlush(module.getModuleAction()
									.cloneProperties());
					config.put(moduleConfiguration.getPath(),
							moduleConfiguration);
				}
			}
			snapshot.setAppConfig(config);

			// Export des containers : commit + push

			Thread.sleep(5000);

			List<String> images = new ArrayList<>();

			for (Server server : application.getServers()) {
				images.add(server.getImage().getPath());
				DockerContainer dockerContainer = new DockerContainer();
				dockerContainer.setName(server.getName());
				dockerContainer.setImage(server.getImage().getName());
				String id = (String) (objectMapper.readValue(DockerContainer
						.commit(dockerContainer, snapshot.getUniqueTagName(),
								application.getManagerHost(), server.getImage()
										.getPath()), HashMap.class)).get("Id");
				DockerContainer.push(server.getImage().getPath(),
						snapshot.getUniqueTagName(),
						application.getManagerHost());
				DockerContainer.deleteImage(id, application.getManagerHost());
			}

			for (Module module : application.getModules()) {

				// commentaire de git
				// if (module.getImage().getPath().contains("git")) {
				// continue;
				// }

				String imageName = "";
				String moduleName = "";
				if (module.getImage().getPath().contains("git")) {
					moduleName = module.getName();
					imageName = module.getImage().getPath();
				} else {
					moduleName = module.getName() + "-data";

					imageName = module.getImage().getPath() + "-"
							+ module.getInstanceNumber() + "-data";
					this.backupModule(module);
				}
				images.add(imageName);
				DockerContainer dockerContainer = new DockerContainer();
				dockerContainer.setName(moduleName);
				dockerContainer.setImage(module.getImage().getName());
				String id = (String) (objectMapper.readValue(
						DockerContainer.commit(dockerContainer,
								snapshot.getUniqueTagName(),
								application.getManagerHost(), imageName),
						HashMap.class)).get("Id");
				DockerContainer.push(imageName, snapshot.getUniqueTagName(),
						application.getManagerHost());
				DockerContainer.deleteImage(id, application.getManagerHost());
			}
			snapshot.setImages(images);
			snapshot = snapshotDAO.save(snapshot);

		} catch (ServiceException | DockerJSONException | InterruptedException
				| IOException | CheckException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
		return snapshot;
	}

	public Snapshot findByTagAndUser(String login, String tag) {
		return snapshotDAO.findByTagAndUser(login, tag);
	}

	@Override
	public List<Snapshot> listAll(String login) throws ServiceException {
		try {
			return snapshotDAO.listAll(login);
		} catch (DataAccessException e) {
			throw new ServiceException("Error : " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional
	public Snapshot remove(String tag, String login) throws ServiceException,
			CheckException {

		Snapshot snapshot = null;
		try {

			snapshot = snapshotDAO.findByTagAndUser(login, tag);

			if (snapshot == null) {
				throw new CheckException("Error : this snapshot doesn't exist");
			}

			List<String> images = snapshotDAO.findAllImagesFromASnapshot(login,
					tag).getImages();

			for (String image : images) {
				DockerContainer.deleteImageIntoTheRegistry(
						image + snapshot.getUniqueTagName(),
						snapshot.getUniqueTagName(),
						env.getProperty("ip.for.registry") + ":5000");
			}
			snapshotDAO.delete(snapshotDAO.findByTagAndUser(login, tag));
		} catch (DataAccessException | DockerJSONException e) {
			throw new ServiceException("Error : " + e.getLocalizedMessage(), e);

		}
		return snapshot;
	}

	@Override
	@Transactional
	public Snapshot cloneFromASnapshot(String applicationName, String tag)
			throws ServiceException, InterruptedException {

		String maxApp = env.getProperty("max.apps");
		String dockerManagerList = env.getProperty("docker.manager.list");
		String dockerManagerPort = env.getProperty("docker.manager.port");
		Snapshot snapshot = null;
		// Tests préliminaires de la création d'une application
		try {
			User user = authentificationUtils.getAuthentificatedUser();
			snapshot = this.findByTagAndUser(user.getLogin(), tag);

			if (applicationName == null | applicationName.equalsIgnoreCase("")) {
				authentificationUtils.allowUser(user);
				throw new CheckException("Please put an app name");
			}

			if (applicationService.countApp(user) >= Integer.parseInt(maxApp)) {
				authentificationUtils.allowUser(user);
				throw new CheckException("You have already created your "
						+ maxApp + " apps into the Cloud");
			}
			if (applicationService.checkAppExist(user, applicationName)) {
				authentificationUtils.allowUser(user);
				throw new CheckException("This application already exists");
			}

			// récupération des images associées

			DockerContainer.pull(imageService.findByName(snapshot.getType())
					.getPath(), snapshot.getUniqueTagName(), dockerManagerList
					+ ":" + dockerManagerPort);
			DockerContainer.pull("cloudunit/git", snapshot.getUniqueTagName(),
					dockerManagerList + ":" + dockerManagerPort);

			// creation de la nouvelle app à partir de l'image taguée

			Application application = applicationService.create(
					applicationName, user.getLogin(), snapshot.getType(),
					snapshot.getUniqueTagName());

			// We need it to get lazy modules relationships
			application = applicationService.findByNameAndUser(
					application.getUser(), application.getName());

			Module moduleGit = moduleService.findGitModule(user.getLogin(),
					application);

			for (Server server : application.getServers()) {
				while (!server.getStatus().equals(Status.START)
						|| !moduleGit.getStatus().equals(Status.START)) {
					Thread.sleep(500);
					logger.info(" wait git and server sshd processus start");
					logger.info("SSHDSTATUS = server : " + server.getStatus()
							+ " - module : " + moduleGit.getStatus());

					moduleGit = moduleService.findById(moduleGit.getId());
					server = serverService.findById(server.getId());
				}
				serverService.update(server,
						snapshot.getJvmMemory().toString(),
						snapshot.getJvmOptions(), snapshot.getJvmRelease(), false);
			}

			restoreModule(snapshot, application, tag);

			application.setDeploymentStatus(snapshot.getDeploymentStatus());
			applicationService.saveInDB(application);

		} catch (ServiceException | CheckException | DockerJSONException e) {
			StringBuilder msgError = new StringBuilder(1024);
			msgError.append("applicationName=[").append(applicationName)
					.append("]");
			msgError.append(", snapshot=[").append(snapshot).append("]");
			msgError.append(", tag=[").append(tag).append("]");
			throw new ServiceException(msgError.toString(), e);
		}
		return snapshot;
	}

	private void backupModule(Module module) {
		Application application;
		try {
			application = applicationService.findByNameAndUser(module
					.getApplication().getUser(), module.getApplication()
					.getName());
			DockerContainer dockerContainer = new DockerContainer();
			dockerContainer.setName(module.getName() + "-data");
			dockerContainer = DockerContainer.findOne(dockerContainer,
					application.getManagerHost());
			Map<String, String> configShell = new HashMap<>();
			configShell.put("port", dockerContainer.getPorts().get("22/tcp"));
			configShell.put("dockerManagerAddress",
					application.getManagerHost());
			String rootPassword = module.getApplication().getUser()
					.getPassword();
			configShell.put("password", rootPassword);
			int code = shellUtils.executeShell(
					"/cloudunit/scripts/backup-data.sh", configShell, true);
			logger.info("The backup script return : " + code);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void restoreModule(Snapshot snapshot, Application application,
			String tag) throws ServiceException {
		String dockerManagerList = env.getProperty("docker.manager.list");
		String dockerManagerPort = env.getProperty("docker.manager.port");

		for (String key : snapshot.getAppConfig().keySet()) {

			try {

				DockerContainer.pull(key, snapshot.getUniqueTagName(),
						dockerManagerList + ":" + dockerManagerPort);
				Module module = ModuleFactory.getModule(snapshot.getAppConfig()
						.get(key).getName());
				module.setApplication(application);
				moduleService.checkImageExist(snapshot.getAppConfig().get(key)
						.getName());
				module.getImage().setName(
						snapshot.getAppConfig().get(key).getName());
				module.setName(snapshot.getAppConfig().get(key).getName());
				module = moduleService.initModule(application, module,
						snapshot.getUniqueTagName());
				Map<String, String> properties = new HashMap<>();
				properties
						.put("username",
								snapshot.getAppConfig()
										.get(key)
										.getProperties()
										.get("username-"
												+ module.getImage().getName()));
				properties
						.put("password",
								snapshot.getAppConfig()
										.get(key)
										.getProperties()
										.get("password-"
												+ module.getImage().getName()));
				properties
						.put("database",
								snapshot.getAppConfig()
										.get(key)
										.getProperties()
										.get("database-"
												+ module.getImage().getName()));
				module.setModuleInfos(properties);
				module = moduleService.saveInDB(module);
				moduleService.stopModule(module);
				moduleService.startModule(module);
				Thread.sleep(5000);

			} catch (DockerJSONException | CheckException
					| InterruptedException e) {
				throw new ServiceException(e.getLocalizedMessage(), e);
			}

		}
	}

	@Override
	public Snapshot findOne(String tag, String login) {
		return snapshotDAO.findByTagAndUser(login, tag);
	}

	private boolean tagExists(String tag, String login) {
		if (snapshotDAO.findByTagAndUser(login, tag) != null) {
			return true;
		}
		return false;
	}
}

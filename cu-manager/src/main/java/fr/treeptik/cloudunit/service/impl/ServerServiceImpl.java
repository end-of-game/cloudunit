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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStopEvent;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dao.ServerDAO;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.AlphaNumericsCharactersCheckUtils;
import fr.treeptik.cloudunit.utils.HipacheRedisUtils;

@Service
public class ServerServiceImpl implements ServerService {

	private Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);

	@Inject
	private ServerDAO serverDAO;

	@Inject
	private ApplicationDAO applicationDAO;

	@Inject
	private HipacheRedisUtils hipacheRedisUtils;

	@Value("${cloudunit.max.servers:1}")
	private String maxServers;

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

	@Inject
	private DockerService dockerService;

	@Inject
	private ApplicationEventPublisher applicationEventPublisher;

	public ServerDAO getServerDAO() {
		return this.serverDAO;
	}

	/**
	 * Save app in just in DB, not create container use principally to charge
	 * status.PENDING of entity until it's really functionnal
	 */
	@Override
	@Transactional
	public Server saveInDB(Server server) throws ServiceException {
		server = serverDAO.save(server);
		return server;
	}

	/**
	 * Create a server with or without a tag. Tag parameter is needed for
	 * restore processus after cloning The idea is to use the same logic for a
	 * new server or another one coming from registry.
	 *
	 * @param server
	 * @param tagName
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@Override
	@Transactional
	public Server create(Server server, String tagName) throws ServiceException, CheckException {

		if (tagName == null) {
			tagName = "";
		}

		logger.debug("create : Methods parameters : " + server);
		logger.info("ServerService : Starting creating Server " + server.getName());

		// General informations
		server.setStatus(Status.PENDING);
		server.setJvmOptions("");
		server.setStartDate(new Date());

		Application application = server.getApplication();
		User user = server.getApplication().getUser();

		// Build a custom container
		String containerName = "";
		try {
			containerName = AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(cuInstanceName.toLowerCase()) + "-"
					+ AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(user.getLogin()) + "-"
					+ AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(server.getApplication().getName()) + "-"
					+ server.getName();
		} catch (UnsupportedEncodingException e2) {
			throw new ServiceException("Error rename Server", e2);
		}

		String imagePath = server.getImage().getPath() + tagName;
		logger.debug("imagePath:" + imagePath);

		String subdomain = System.getenv("CU_SUB_DOMAIN");
		if (subdomain == null) {
			subdomain = "";
		}
		logger.info("env.CU_SUB_DOMAIN=" + subdomain);

		server.getApplication().setSuffixCloudUnitIO(subdomain + suffixCloudUnitIO);

		try {
			dockerService.createServer(containerName, server, imagePath, user);
			server = dockerService.startServer(containerName, server);
			server = serverDAO.saveAndFlush(server);

			logger.info(dockerService.getEnv(server.getContainerID(), "CU_SERVER_PORT"));
			logger.info(dockerService.getEnv(server.getContainerID(), "CU_SERVER_MANAGER_PORT"));
			logger.info(application.getLocation());

			hipacheRedisUtils.createRedisAppKey(server.getApplication(), server.getContainerIP(),
					dockerService.getEnv(server.getContainerID(), "CU_SERVER_PORT"),
					dockerService.getEnv(server.getContainerID(), "CU_SERVER_MANAGER_PORT"));

			// Update server with all its informations
			server.setManagerLocation("http://manager-" + application.getLocation().substring(7)
					+ dockerService.getEnv(server.getContainerID(), "CU_SERVER_MANAGER_PATH"));
			server.setStatus(Status.START);
			server.setJvmMemory(512L);
			server.setJvmRelease(dockerService.getEnv(server.getContainerID(), "CU_DEFAULT_JAVA_RELEASE"));

			server = this.update(server);

			Map<String, String> kvStore = new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					put("CU_USER", user.getLogin());
					put("CU_PASSWORD", user.getPassword());
				}
			};
			dockerService.execCommand(server.getContainerID(), RemoteExecAction.ADD_USER.getCommand(kvStore));
			applicationEventPublisher.publishEvent(new ServerStartEvent(server));

		} catch (PersistenceException e) {
			logger.error("ServerService Error : Create Server " + e);
			// Removing a creating container if an error has occurred with
			// the database
			// DockerContainer.remove(dockerContainer,
			// application.getManagerIp());

			throw new ServiceException(e.getLocalizedMessage(), e);
		} catch (DockerJSONException e) {
			StringBuilder msgError = new StringBuilder(512);
			msgError.append("server=").append(server);
			msgError.append(", tagName=[").append(tagName).append("]");
			logger.error("" + msgError, e);
			throw new ServiceException(msgError.toString(), e);
		}
		logger.info("ServerService : Server " + server.getName() + " successfully created.");
		return server;
	}

	/**
	 * check if the status passed in parameter is the same as in db if it's case
	 * a checkException is throws
	 *
	 * @throws ServiceException
	 *             CheckException
	 */
	@Override
	public void checkStatus(Server server, String status) throws CheckException {
		if (server.getStatus().name().equalsIgnoreCase(status)) {
			throw new CheckException("Error : Server " + server.getName() + " is already " + status + "ED");
		}
	}

	/**
	 * check if the status is PENDING return TRUE else return false
	 *
	 * @throws ServiceException
	 *             CheckException
	 */
	@Override
	public boolean checkStatusPENDING(Server server) throws ServiceException {
		logger.info("--CHECK SERVER STATUS PENDING--");

		if (server.getStatus().name().equalsIgnoreCase("PENDING")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	@Transactional
	public Server update(Server server) throws ServiceException {

		logger.info("ServerService : Starting updating Server " + server.getName());
		try {
			server = serverDAO.save(server);

			Application application = server.getApplication();

			hipacheRedisUtils.updateServerAddress(application, server.getContainerIP(),
					dockerService.getEnv(server.getContainerID(), "CU_SERVER_PORT"),
					dockerService.getEnv(server.getContainerID(), "CU_SERVER_MANAGER_PORT"));

		} catch (PersistenceException | FatalDockerJSONException e) {
			logger.error("ServerService Error : update Server" + e);
			throw new ServiceException("Error database : " + e.getLocalizedMessage(), e);
		}

		logger.info("ServerService : Server " + server.getName() + " successfully updated.");

		return server;
	}

	@Override
	@Transactional
	public Server remove(String serverName) throws ServiceException {
		Server server = null;
		try {
			server = this.findByName(serverName);

			// check if there is no action currently on the entity
			if (this.checkStatusPENDING(server)) {
				return null;
			}
			Application application = server.getApplication();

			dockerService.removeServer(server.getName());

			// Remove server on cloudunit :
			hipacheRedisUtils.removeServerAddress(application);

			serverDAO.delete(server);

			logger.info("ServerService : Server successfully removed ");

		} catch (PersistenceException e) {
			logger.error("Error database :  " + server.getName() + " : " + e);
			throw new ServiceException("Error database :  " + e.getLocalizedMessage(), e);
		} catch (DockerJSONException e) {
			logger.error(serverName, e);
		}
		return server;
	}

	@Override
	public Server findById(Integer id) throws ServiceException {
		try {
			logger.debug("findById : Methods parameters : " + id);
			Server server = serverDAO.findOne(id);
			if (server != null) {
				logger.info("Server with id " + id + " found!");
				logger.info("" + server);
			}
			return server;
		} catch (PersistenceException e) {
			logger.error("Error ServerService : error findById Method : " + e);
			throw new ServiceException("Error database :  " + e.getLocalizedMessage(), e);

		}
	}

	@Override
	public List<Server> findAll() throws ServiceException {
		try {
			logger.debug("start findAll");
			List<Server> servers = serverDAO.findAll();
			logger.info("ServerService : All Servers found ");
			return servers;
		} catch (PersistenceException e) {
			logger.error("Error ServerService : error findAll Method : " + e);
			throw new ServiceException("Error database :  " + e.getLocalizedMessage(), e);

		}
	}

	@Override
	@Transactional
	public Server startServer(Server server) throws ServiceException {

		logger.info("ServerService : Starting Server " + server.getName());
		try {
			Application application = server.getApplication();

			// Call the hook for pre start
			server.setStartDate(new Date());
			applicationDAO.saveAndFlush(application);
			server = update(server);
			server = dockerService.startServer(server.getContainerID(), server);
			hipacheRedisUtils.updateServerAddress(server.getApplication(), server.getContainerIP(),
					dockerService.getEnv(server.getContainerID(), "CU_SERVER_PORT"),
					dockerService.getEnv(server.getContainerID(), "CU_SERVER_MANAGER_PORT"));

			applicationEventPublisher.publishEvent(new ServerStartEvent(server));

		} catch (DockerJSONException e) {
			e.printStackTrace();
			throw new ServiceException(server.toString(), e);
		} catch (PersistenceException e) {
			throw new ServiceException(server.toString(), e);
		}
		return server;
	}

	@Override
	@Transactional
	public Server stopServer(Server server) throws ServiceException {
		try {
			dockerService.stopServer(server.getName());
			applicationEventPublisher.publishEvent(new ServerStopEvent(server));
		} catch (PersistenceException e) {
			throw new ServiceException(server.toString(), e);
		} catch (DockerJSONException e) {
			throw new ServiceException(server.toString(), e);
		}
		return server;
	}

	@Override
	@Transactional
	public Server restartServer(Server server) throws ServiceException {
		server = this.stopServer(server);
		server = this.startServer(server);
		return server;
	}

	@Override
	public Server findByName(String serverName) throws ServiceException {
		try {
			return serverDAO.findByName(serverName);
		} catch (PersistenceException e) {
			throw new ServiceException("Error database : " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Server findByApp(Application application) throws ServiceException {
		try {
			return serverDAO.findByApp(application.getId());
		} catch (PersistenceException e) {
			throw new ServiceException("Error database : " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Server findByContainerID(String id) throws ServiceException {
		try {
			return serverDAO.findByContainerID(id);
		} catch (PersistenceException e) {
			throw new ServiceException("Error database : " + e.getLocalizedMessage(), e);
		}
	}

	@Override
	@Transactional
	public Server update(Server server, String jvmMemory, String options, String jvmRelease, boolean restorePreviousEnv)
			throws ServiceException {

		String previousJvmMemory = server.getJvmMemory().toString();
		String previousJvmRelease = server.getJvmRelease();
		String previousJvmOptions = server.getJvmOptions();
		
		options = options == null ? "" : options;
		
		final String jvmOptions = options.replaceAll("//", "\\\\/\\\\/");

		try {
			// If jvm memory or options changes...
			/*
			if (!jvmMemory.equalsIgnoreCase(server.getJvmMemory().toString())
					|| !jvmOptions.equalsIgnoreCase(server.getJvmOptions())) {
				Map<String, String> kvStore = new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put("MEMORY_VALUE", jvmMemory);
						put("JVM_OPTIONS", jvmOptions);
					}
				};
				dockerService.execCommand(server.getContainerID(),
						RemoteExecAction..getCommand(kvStore));
			}
			*/
			// If jvm release changes...
			if (!jvmRelease.equalsIgnoreCase(server.getJvmRelease())) {
				changeJavaVersion(server.getApplication(), jvmRelease);
			}

			server.setJvmMemory(Long.valueOf(jvmMemory));
			server.setJvmOptions(jvmOptions);
			server.setJvmRelease(jvmRelease);
			server = saveInDB(server);

		} catch (Exception e) {
			if (!restorePreviousEnv) {
				StringBuilder msgError = new StringBuilder();
				msgError.append("jvmMemory:").append(jvmMemory).append(",");
				msgError.append("jvmOptions:").append(jvmOptions).append(",");
				msgError.append("jvmRelease:").append(jvmRelease);
				throw new ServiceException(msgError.toString(), e);
			} else {
				// Rollback to previous configuration
				logger.warn("Restore the previous environment for jvm configuration. Maybe a syntax error");
				update(server, previousJvmMemory, previousJvmOptions, previousJvmRelease, false);
			}
		}

		return server;

	}

	/**
	 * Change the version of the jvm
	 *
	 * @param application
	 * @param javaVersion
	 * @throws CheckException
	 * @throws ServiceException
	 */
	@Override
	public void changeJavaVersion(Application application, String javaVersion) throws CheckException, ServiceException {
		logger.info("Starting changing to java version " + javaVersion + ", the application " + application.getName());
		try {
			// todo
		} catch (Exception e) {
			throw new ServiceException(application + ", javaVersion:" + javaVersion, e);
		}

	}

}

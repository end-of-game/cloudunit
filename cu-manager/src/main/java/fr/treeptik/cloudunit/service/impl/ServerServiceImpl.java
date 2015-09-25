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
import fr.treeptik.cloudunit.dao.ServerDAO;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.DockerContainerBuilder;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServerServiceImpl implements ServerService {

    private Logger logger = LoggerFactory.getLogger(ServerServiceImpl.class);

    @Inject
    private ServerDAO serverDAO;

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private UserService userService;

    @Inject
    private ModuleService moduleService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private ShellUtils shellUtils;

    @Inject
    private HipacheRedisUtils hipacheRedisUtils;

    @Inject
    private ContainerMapper containerMapper;

    @Value("${cloudunit.max.servers:1}")
    private String maxServers;

    @Value("${suffix.cloudunit.io}")
    private String suffixCloudUnitIO;

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
     * Create a server with or without a tag.
     * Tag parameter is needed for restore processus after cloning
     * The idea is to use the same logic for a new server or another one coming from registry.
     *
     * @param server
     * @param tagName
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @Override
    @Transactional
    public Server create(Server server, String tagName)
            throws ServiceException, CheckException {

        String registryPrefix = "";

        if (tagName != null) {
            registryPrefix = "localhost:5000/";
        } else {
            tagName = "";
        }

        logger.debug("create : Methods parameters : " + server);
        logger.info("ServerService : Starting creating Server "
                + server.getName());

        // Initialize container informations :
        DockerContainer dockerContainer = new DockerContainer();
        Map<String, String> ports = new HashMap<String, String>();

        // General informations
        String dockerManagerIP = server.getApplication().getManagerIp();
        server.setStatus(Status.PENDING);
        server.setJvmOptions("");
        server.setStartDate(new Date());

        Application application = server.getApplication();
        User user = server.getApplication().getUser();

        // Build a custom container
        String containerName = "";
        try {
            containerName = AlphaNumericsCharactersCheckUtils
                    .convertToAlphaNumerics(user.getLogin())
                    + "-"
                    + AlphaNumericsCharactersCheckUtils
                    .convertToAlphaNumerics(server.getApplication()
                            .getName()) + "-" + server.getName();
        } catch (UnsupportedEncodingException e2) {
            throw new ServiceException("Error rename Serveur", e2);
        }

        String imagePath = registryPrefix + server.getImage().getPath()
                + tagName.replace(":", "") + tagName;

        logger.debug("imagePath:" + imagePath);

        List<String> volumesFrom = new ArrayList<>();
        volumesFrom.add(server.getImage().getName());
        volumesFrom.add("java");
        dockerContainer = new DockerContainerBuilder()
                .withName(containerName)
                .withImage(imagePath)
                .withMemory(0L)
                .withMemorySwap(0L)
                .withPorts(ports)
                .withVolumesFrom(volumesFrom)
                .withCmd(
                        Arrays.asList(user.getLogin(), user.getPassword(), server
                                        .getApplication().getRestHost(), server
                                        .getApplication().getName(),
                                "jdk1.7.0_55")).build();

        try {
            // create a container and get informations
            DockerContainer.create(dockerContainer,
                    application.getManagerIp());

            logger.debug("container : " + dockerContainer);

            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());

            String subdomain = System.getenv("CU_SUB_DOMAIN");
            if (subdomain == null) {
                subdomain = "";
            }
            logger.info("env.CU_SUB_DOMAIN=" + subdomain);

            server.getApplication().setSuffixCloudUnitIO(subdomain + suffixCloudUnitIO);
            DockerContainer.start(dockerContainer, application.getManagerIp());
            dockerContainer = DockerContainer.findOne(dockerContainer, application.getManagerIp());

            server = containerMapper.mapDockerContainerToServer(dockerContainer, server);
            server = serverDAO.saveAndFlush(server);
            server = ServerFactory.updateServer(server);

            logger.info(server.getServerAction().getServerManagerPath());
            logger.info("" + server.getListPorts());
            logger.info(server.getServerAction().getServerManagerPort());
            logger.info(application.getLocation());

            hipacheRedisUtils.createRedisAppKey(server.getApplication(),
                    server.getContainerIP(), server.getServerAction()
                            .getServerPort(), server.getServerAction()
                            .getServerManagerPort());

            // Update server with all its informations
            server.setManagerLocation("http://manager-"
                    + application.getLocation().substring(7)
                    + server.getServerAction().getServerManagerPath());
            server.setStatus(Status.START);
            server.setJvmMemory(512L);
            server.setJvmRelease("jdk1.7.0_55");

            server = this.update(server);

            Thread.sleep(3000);

        } catch (PersistenceException e) {
            logger.error("ServerService Error : Create Server " + e);
            try {
                // Removing a creating container if an error has occurred with
                // the database
                DockerContainer.remove(dockerContainer,
                        application.getManagerIp());
            } catch (DockerJSONException e1) {
                logger.error("ServerService Error : Create Server " + e1);
                throw new ServiceException(e.getLocalizedMessage(), e1);
            }
            throw new ServiceException(e.getLocalizedMessage(), e);
        } catch (DockerJSONException e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append("server=").append(server);
            msgError.append(", tagName=[").append(tagName).append("]");
            logger.error("" + msgError, e);
            throw new ServiceException(msgError.toString(), e);
        } catch (InterruptedException e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append("server=").append(server);
            msgError.append(", tagName=[").append(tagName).append("]");
            logger.error("" + msgError, e);
        }
        logger.info("ServerService : Server " + server.getName()
                + " successfully created.");
        return server;
    }

    /**
     * Test if the user can create new server associated to this application
     *
     * @param application
     * @throws ServiceException
     * @throws CheckException
     */
    public void checkMaxNumberReach(Application application)
            throws ServiceException, CheckException {
        logger.info("check number of server of " + application.getName());
        if (application.getServers() != null) {
            try {
                if (application.getServers().size() >= Integer
                        .parseInt(maxServers)) {
                    throw new CheckException("You have already created your "
                            + maxServers + " server for your application");
                }
            } catch (PersistenceException e) {
                logger.error("ServerService Error : check number of server" + e);
                throw new ServiceException(e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * check if the status passed in parameter is the same as in db if it's case
     * a checkException is throws
     *
     * @throws ServiceException CheckException
     */
    @Override
    public void checkStatus(Server server, String status) throws CheckException {
        if (server.getStatus().name().equalsIgnoreCase(status)) {
            throw new CheckException("Error : Server " + server.getName()
                    + " is already " + status + "ED");
        }
    }

    /**
     * check if the status is PENDING return TRUE else return false
     *
     * @throws ServiceException CheckException
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

        logger.debug("update : Methods parameters : " + server.toString());
        logger.info("ServerService : Starting updating Server "
                + server.getName());
        try {
            server = serverDAO.save(server);

            Application application = server.getApplication();
            String dockerManagerIP = application.getManagerIp();

            hipacheRedisUtils.updateServerAddress(application, server
                            .getContainerIP(),
                    server.getServerAction().getServerPort(), server
                            .getServerAction().getServerManagerPort());

        } catch (PersistenceException e) {
            logger.error("ServerService Error : update Server" + e);
            throw new ServiceException("Error database : "
                    + e.getLocalizedMessage(), e);
        }

        logger.info("ServerService : Server " + server.getName()
                + " successfully updated.");

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

            // Remove container on docker manager :
            DockerContainer dockerContainer = new DockerContainer();
            dockerContainer.setName(server.getName());
            dockerContainer.setImage(server.getImage().getName());

            if (server.getStatus().equals(Status.START)) {
                DockerContainer.stop(dockerContainer,
                        application.getManagerIp());
                Thread.sleep(1000);
            }

            server.setStatus(Status.PENDING);
            server = this.saveInDB(server);

            String imageName = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp()).getImage();

            DockerContainer.remove(dockerContainer,
                    application.getManagerIp());

            try {
                if (application.isAClone()) {
                    DockerContainer.deleteImage(imageName,
                            application.getManagerIp());
                }
            } catch (DockerJSONException e) {
                logger.info("Others apps use this docker images");
            }

            // Remove server on cloudunit :
            hipacheRedisUtils.removeServerAddress(application);

            serverDAO.delete(server);

            logger.info("ServerService : Server successfully removed ");

        } catch (PersistenceException e) {
            logger.error("Error database :  " + server.getName() + " : " + e);
            throw new ServiceException("Error database :  "
                    + e.getLocalizedMessage(), e);
        } catch (DockerJSONException e) {
            logger.error("ServerService Error : fail to remove Server" + e);
            throw new ServiceException("Error docker :  "
                    + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            throw new ServiceException("Error database :  "
                    + e.getLocalizedMessage(), e);

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
            throw new ServiceException("Error database :  "
                    + e.getLocalizedMessage(), e);

        }
    }

    @Override
    public List<Server> findAllStatusStartServers() throws ServiceException {
        List<Server> listServers = this.findAll();
        List<Server> listStatusStopServers = new ArrayList<>();

        for (Server server : listServers) {
            if (Status.START == server.getStatus()) {
                listStatusStopServers.add(server);
            }
        }
        return listStatusStopServers;
    }

    @Override
    public List<Server> findAllStatusStopServers() throws ServiceException {
        List<Server> listServers = this.findAll();
        List<Server> listStatusStopServers = new ArrayList<>();

        for (Server server : listServers) {
            if (Status.STOP == server.getStatus()) {
                listStatusStopServers.add(server);
            }
        }
        return listStatusStopServers;
    }

    @Override
    @Transactional
    public Server startServer(Server server) throws ServiceException {

        logger.debug("start : Methods parameters : " + server);
        logger.info("ServerService : Starting Server " + server.getName());

        try {
            Application application = server.getApplication();

            DockerContainer dockerContainer = new DockerContainer();
            dockerContainer.setName(server.getName());
            dockerContainer.setImage(server.getImage().getName());
            if (server.getPortsToOpen() != null) {
                dockerContainer.setPortsToOpen(server.getPortsToOpen().stream()
                        .map(t -> Integer.parseInt(t.getPort()))
                        .collect(Collectors.toList()));
            }
            DockerContainer
                    .start(dockerContainer, application.getManagerIp());
            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());

            server = containerMapper.mapDockerContainerToServer(
                    dockerContainer, server);

            String dockerManagerIP = server.getApplication().getManagerIp();

            server.setStatus(Status.START);
            server.setStartDate(new Date());
            application = applicationDAO.saveAndFlush(application);

            server = this.update(server);
            hipacheRedisUtils.updateServerAddress(server.getApplication(),
                    server.getContainerIP(), server.getServerAction()
                            .getServerPort(), server.getServerAction()
                            .getServerManagerPort());

            // ajout des alias des ports forwardés
            final Application effectiveApplication = application;
            final Server effectiveServer = server;
            server.getPortsToOpen()
                    .stream()
                    .filter(t -> t.getAlias() != null)
                    .forEach(
                            t -> hipacheRedisUtils.writeNewAlias(
                                    t.getPort(),
                                    effectiveApplication,
                                    effectiveServer.getListPorts().get(
                                            t + "/tcp")));

        } catch (PersistenceException e) {
            logger.error("ServerService Error : fail to start Server" + e);
            throw new ServiceException("Error database :  "
                    + e.getLocalizedMessage(), e);
        } catch (DockerJSONException e) {
            logger.error("ServerService Error : fail to start Server" + e);
            throw new ServiceException("Error docker :  "
                    + e.getLocalizedMessage(), e);
        }
        return server;
    }

    @Override
    @Transactional
    public Server stopServer(Server server) throws ServiceException {
        try {
            Application application = server.getApplication();

            DockerContainer dockerContainer = new DockerContainer();
            dockerContainer.setName(server.getName());
            dockerContainer.setImage(server.getImage().getName());
            DockerContainer.stop(dockerContainer, application.getManagerIp());
            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());
            server.setDockerState(dockerContainer.getState());

            server.setStatus(Status.STOP);
            server = this.update(server);
        } catch (PersistenceException e) {
            throw new ServiceException("Error database : "
                    + e.getLocalizedMessage(), e);
        } catch (DockerJSONException e) {
            logger.error("Fail to stop Server : " + e);
            throw new ServiceException("Error docker : "
                    + e.getLocalizedMessage(), e);
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
            throw new ServiceException("Error database : "
                    + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<Server> findByApp(Application application)
            throws ServiceException {
        try {
            return serverDAO.findByApp(application.getId());
        } catch (PersistenceException e) {
            throw new ServiceException("Error database : "
                    + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Server findByContainerID(String id) throws ServiceException {
        try {
            return serverDAO.findByContainerID(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error database : "
                    + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Server update(Server server, String jvmMemory, String jvmOptions,
                         String jvmRelease, boolean restorePreviousEnv) throws ServiceException {


        Map<String, String> configShell = new HashMap<>();
        configShell.put("port", server.getSshPort());
        configShell.put("dockerManagerAddress", server.getApplication().getManagerIp());
        // We don't need to set userLogin because shell script caller must be root.
        configShell.put("password", server.getApplication().getUser().getPassword());

        String previousJvmOptions = server.getJvmOptions();
        String previousJvmMemory = server.getJvmMemory().toString();
        String previousJvmRelease = server.getJvmRelease();

        try {

            // If jvm memory or options changes...
            if (!jvmMemory.equalsIgnoreCase(server.getJvmMemory().toString())
                    || !jvmOptions.equalsIgnoreCase(server.getJvmOptions())) {
                // Changement configuration MEMOIRE + OPTIONS
                String command = "bash /cloudunit/appconf/scripts/change-server-config.sh "
                        + jvmMemory + " " + "\"" + jvmOptions + "\"";
                logger.info("command shell to execute [" + command + "]");
                shellUtils.executeShell(command, configShell);
            }

            // If jvm release changes...
            if (!jvmRelease.equalsIgnoreCase(server.getJvmRelease())) {
                changeJavaVersion(server.getApplication(), jvmRelease);
            }

            server.setJvmMemory(Long.valueOf(jvmMemory));
            server.setJvmOptions(jvmOptions);
            server.setJvmRelease(jvmRelease);
            server = this.saveInDB(server);

        } catch (Exception e) {
            // Exception would be one RuntimeException coming from shell error
            // If second call and no way to start gracefully tomcat, we need to stop application
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
    public void changeJavaVersion(Application application, String javaVersion)
            throws CheckException, ServiceException {

        logger.info("Starting changing to java version " + javaVersion
                + ", the application " + application.getName());

        Map<String, String> configShell = new HashMap<>();
        String command = null;

        // Servers
        List<Server> listServers = application.getServers();
        for (Server server : listServers) {
            try {
                configShell.put("password", server.getApplication().getUser().getPassword());
                configShell.put("port", server.getSshPort());
                configShell.put("dockerManagerAddress", application.getManagerIp());

                // Need to be root for shell call because we modify /etc/environme,t
                command = "bash /cloudunit/scripts/change-java-version.sh " + javaVersion;
                logger.info("command shell to execute [" + command + "]");
                shellUtils.executeShell(command, configShell);

            } catch (Exception e) {
                server.setStatus(Status.FAIL);
                saveInDB(server);
                logger.error("java version = " + javaVersion + " - " + application.toString() + " - " + server.toString(), e);
                throw new ServiceException(application+", javaVersion:"+javaVersion, e);
            }
        }

        //
        // PARTIE GIT
        //
        Module moduleGit = moduleService.findGitModule(application.getUser()
                .getLogin(), application);
        try {
            configShell.put("password", moduleGit.getApplication().getUser()
                    .getPassword());
            configShell.put("port", moduleGit.getSshPort());
            configShell.put("dockerManagerAddress",
                    application.getManagerIp());

            // Besoin des permissions ROOT
            command = "bash /cloudunit/scripts/change-java-version.sh "
                    + javaVersion;
            logger.info("command shell to execute [" + command + "]");

            shellUtils.executeShell(command, configShell);

            application.setJvmRelease(javaVersion);
            application = applicationService.saveInDB(application);
        } catch (Exception e) {
            moduleGit.setStatus(Status.FAIL);
            moduleService.saveInDB(moduleGit);
            logger.error(
                    "java version = " + javaVersion + " - "
                            + application.toString() + " - "
                            + moduleGit.toString(), e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

    }


    /**
     * Méthode permettant de mettre le server dans un état particulier pour se
     * prémunir d'éventuel problème de concurrence au niveau métier
     */
    @Override
    public Server confirmSSHDStart(String applicationName, String userLogin)
            throws ServiceException {

        logger.debug("Start confirmSSHDStart - applicationName : "
                + applicationName + " - userLogin :" + userLogin);

        Application application = null;
        Server server = null;
        try {
            User user = userService.findByLogin(userLogin);
            while (application == null) {
                try {
                    application = applicationService.findByNameAndUser(user,
                            applicationName);
                } catch (Exception e) {
                    continue;
                }
            }
            /**
             * TODO : REFACTOR quand on pourra avoir plusieurs instances de
             * serveur
             */
            server = this.findByApp(application).get(0);
            server.setStatus(Status.START);
            server = this.saveInDB(server);
        } catch (PersistenceException e) {
            e.printStackTrace();
            logger.error("Error ServerService : error set server on sshdStatus "
                    + Status.START + " : " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return server;
    }

    @Transactional
    @Override
    public void openPort(String applicationName, String port, String alias,
                         boolean isRunning) throws ServiceException {
        try {
            Server server = this.findByApp(
                    applicationService.findByNameAndUser(
                            authentificationUtils.getAuthentificatedUser(),
                            applicationName)).get(0);

            if (server.getPortsToOpen() == null) {
                server.setPortsToOpen(new ArrayList<PortToOpen>());
            }
            server.getPortsToOpen().add(new PortToOpen(port, alias));
            server = update(server);

            if (isRunning) {
                stopServer(server);
                startServer(server);
            } else {
                startServer(server);
            }

        } catch (ServiceException | CheckException e) {
            throw new ServiceException("error open port", e);
        }
    }

    @Transactional
    @Override
    public void closePort(String applicationName, String port, String alias,
                          boolean isRunning) throws ServiceException {
        try {
            Server server = this.findByApp(
                    applicationService.findByNameAndUser(
                            authentificationUtils.getAuthentificatedUser(),
                            applicationName)).get(0);

            if (server.getPortsToOpen() == null) {
                server.setPortsToOpen(new ArrayList<PortToOpen>());
            }
            server.getPortsToOpen().remove(
                    server.getPortsToOpen().stream()
                            .filter(t -> t.getPort().equals(port)).findAny());

            server = update(server);

            if (isRunning) {
                stopServer(server);
                startServer(server);
            } else {
                startServer(server);
            }

        } catch (ServiceException | CheckException e) {
            throw new ServiceException("error open port", e);
        }
    }
}

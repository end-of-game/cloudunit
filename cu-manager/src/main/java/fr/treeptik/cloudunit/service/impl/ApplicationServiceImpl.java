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

import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dao.ImageDAO;
import fr.treeptik.cloudunit.dao.PortToOpenDAO;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
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
import java.util.*;

@Service
public class ApplicationServiceImpl
        implements ApplicationService {

    Locale locale = Locale.ENGLISH;

    private Logger logger = LoggerFactory
            .getLogger(ApplicationServiceImpl.class);

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private ImageDAO imageDAO;

    @Inject
    private PortToOpenDAO portToOpenDAO;

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
    private ContainerMapper containerMapper;

    @Inject
    private UserService userService;

    @Inject
    private MessageSource messageSource;

    @Value("${cloudunit.max.apps:100}")
    private String numberMaxApplications;

    @Value("${docker.manager.ip:192.168.50.4:2376}")
    private String dockerManagerIp;

    @Value("${suffix.cloudunit.io}")
    private String suffixCloudUnitIO;

    @Value("${java.version.default}")
    private String javaVersionDefault;

    @Value("${cloudunit.manager.ip}")
    private String restHost;

    @Value("${cloudunit.instance.name}")
    private String cuInstanceName;

    @Value("${cloudunit.manager.ip}")
    private String hostName2;

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
            if (checkAppExist(application.getUser(), application.getName())) {
                throw new CheckException(messageSource.getMessage("app.exists",
                        null, locale));
            }
            if (checkNameLength(application.getName())) {
                throw new CheckException("This name has length equal to zero : " + application.getName());
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
    public Application saveInDB(Application application)
            throws ServiceException {
        logger.debug("-- SAVE -- : " + application);
        // Do not affect application with save return.
        // You could lose the relationships.
        applicationDAO.save(application);
        return application;
    }

    /* Methode qui teste la validité d'une application
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
        application.setDisplayName(applicationName);
        application.setUser(user);
        application.setModules(new ArrayList<>());

        this.checkCreate(application, serverName);
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public Application create(String applicationName, String login, String serverName, String tagName, String origin)
            throws ServiceException,
            CheckException {

        // if tagname is null, we prefix with a ":"
        if (tagName != null) {
            tagName = ":" + tagName;
        }

        logger.info("--CALL CREATE NEW APP--");
        Application application = new Application();

        logger.info("applicationName = " + applicationName + ", serverName = "
                + serverName);

        User user = authentificationUtils.getAuthentificatedUser();

        // For cloning management
        if (tagName != null) {
            application.setAClone(true);
            application.setOrigin(origin);
        }

        application.setName(applicationName);
        application.setDisplayName(applicationName);
        application.setUser(user);
        application.setCuInstanceName(cuInstanceName);
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
            application.setDomainName(subdomain + suffixCloudUnitIO);
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

            // Persistence for Application model
            application = applicationDAO.save(application);

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
            throws ServiceException, CheckException {

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

            for (PortToOpen portToOpen : application.getPortsToOpen()) {
                removePort(application, portToOpen.getPort());
            }

            // Delete all servers
            List<Server> listServers = application.getServers();
            for (Server server : listServers) {
                serverService.remove(server.getName());
                if (listServers.indexOf(server) == listServers.size() - 1) {
                    hipacheRedisUtils.removeRedisAppKey(application);
                    applicationDAO.delete(server.getApplication());
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
    public Application start(Application application)
            throws ServiceException {
        try {
            User user = authentificationUtils.getAuthentificatedUser();
            logger.debug("start : Methods parameters : " + application);

            List<Module> modules = application.getModules();
            for (Module module : modules) {
                try {
                    module = moduleService.startModule(module);
                } catch (ServiceException e) {
                    logger.error("failed to start "
                            + application.toString(), e);
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

            application.getPortsToOpen().stream().forEach(p -> updatePortAlias(p, application));

            logger.info("ApplicationService : Application successfully started ");
        } catch (PersistenceException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return application;
    }


    @Override
    @Transactional
    public Application stop(Application application)
            throws ServiceException {

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
    public List<Application> findAll()
            throws ServiceException {
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
    public List<Application> findAllByUser(User user)
            throws ServiceException {
        try {
            List<Application> applications = applicationDAO.findAllByUser(user.getId(), cuInstanceName);
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
                    user.getId(), name, cuInstanceName);

            return application;

        } catch (PersistenceException e) {
            logger.error(user.toString(), e);
            throw new ServiceException(user.toString(), e);
        }
    }

    @Transactional
    public void deployToContainerId(String applicationName, String containerId,
                                    File file, String destFile)
            throws ServiceException {

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

        int code = -1;
        Map<String, String> configShell = new HashMap<>();

        try {
            // get app with all its components
            for (Server server : application.getServers()) {

                // loading server ssh informations
                String rootPassword = server.getApplication().getUser() .getPassword();
                configShell.put("port", server.getSshPort());
                configShell.put("dockerManagerAddress", application.getManagerIp());
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
                throw new ServiceException("No way to deploy application " + file + ", " + application);
            }

        } catch (Exception e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

        return application;
    }

    @Override
    public Long countApp(User user)
            throws ServiceException {
        try {
            return applicationDAO.countApp(user.getId(), cuInstanceName);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Liste des containers pour une application
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
                                              boolean withModules)
            throws ServiceException {
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
                                         boolean withModules)
            throws ServiceException {
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
            return applicationDAO.findAllAliases(application.getName(), cuInstanceName);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    @Transactional
    public void addNewAlias(Application application, String alias)
            throws ServiceException, CheckException {

        logger.info("ALIAS VALUE IN addNewAlias : " + alias);

        alias = alias.toLowerCase();
        if (alias.startsWith("https://") || alias.startsWith("http://")
                || alias.startsWith("ftp://")) {
            alias = alias
                    .substring(alias.lastIndexOf("//") + 2, alias.length());
        }

        if (!DomainUtils.isValidDomainName(alias)) {
            throw new CheckException(messageSource.getMessage("alias.invalid", null, locale));
        }

        if (checkAliasIfExists(alias)) {
            throw new CheckException(messageSource.getMessage("alias.exists", null, locale));
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
    public void updateAliases(Application application)
            throws ServiceException {
        try {
            Server server = application.getServers().get(0);
            List<String> aliases = applicationDAO.findAllAliases(application
                    .getName(), cuInstanceName);
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
            throws ServiceException, CheckException {
        try {
            hipacheRedisUtils.removeAlias(alias);
            boolean removed = application.getAliases().remove(alias);
            if (!removed) {
                throw new CheckException("Alias [" + alias + "] doesn't exist");
            }
            application = applicationDAO.save(application);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Transactional
    @Override
    public void addPort(Application application, String nature, Integer port) throws ServiceException {
        PortToOpen portToOpen = new PortToOpen();
        portToOpen.setNature(nature);
        portToOpen.setPort(port);
        portToOpen.setApplication(application);
        try {
            String alias = null;
            // add the port alias for http mode only
            if ("web".equalsIgnoreCase(portToOpen.getNature())) {
                hipacheRedisUtils.writeNewAlias((application.getName()
                                + "-" + application.getUser().getLogin() + "-"
                                + "forward-" + portToOpen.getPort() + application.getSuffixCloudUnitIO()),
                        application,
                        portToOpen.getPort().toString());
                alias = "http://" + application.getName()
                        + "-" + application.getUser().getLogin() + "-"
                        + "forward-" + portToOpen.getPort() + application.getDomainName();
            } else if ("other".equalsIgnoreCase(portToOpen.getNature())) {
                alias = application.getServers().iterator().next().getName()
                        + "."
                        + application.getServers().iterator().next().getImage().getPath().substring(10)
                        + ".cloud.unit";
            }
            portToOpen.setAlias(alias);
            portToOpenDAO.save(portToOpen);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }


    public void updatePortAlias(PortToOpen portToOpen, Application application) {
        if ("web".equalsIgnoreCase(portToOpen.getNature())) {
            hipacheRedisUtils.updatePortAlias(
                    application.getServers().get(0).getContainerIP(),
                    portToOpen.getPort(),
                    portToOpen.getAlias().substring(portToOpen.getAlias().lastIndexOf("//") + 2));
        }
    }

    @Transactional
    @Override
    public void removePort(Application application, Integer port) throws CheckException, ServiceException {

        PortToOpen portToOpen = application.getPortsToOpen()
                .stream()
                .filter(p -> p.getPort().equals(port))
                .findFirst()
                .orElseThrow(() -> new CheckException("Port[" + port + "] is not bound to this application"));

        try {
            if ("web".equalsIgnoreCase(portToOpen.getNature())) {
                hipacheRedisUtils.removeServerPortAlias(portToOpen.getAlias().substring(7));
            }
            portToOpenDAO.delete(portToOpen);
            saveInDB(application);
        } catch (DataAccessException e) {
            throw new ServiceException(e.getMessage(), e);
        }

    }

    private boolean checkAliasIfExists(String alias) {
        if (applicationDAO.findAliasesForAllApps().contains(alias)) {
            return true;
        }
        return false;
    }

    public Integer countApplicationsForImage(String cuInstanceName, User user, String tag) throws CheckException, ServiceException {
        return applicationDAO.countAppForTagLike(cuInstanceName, user.getLogin(), tag);
    }

}

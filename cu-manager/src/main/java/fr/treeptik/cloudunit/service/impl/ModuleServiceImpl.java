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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dao.ModuleDAO;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.DockerContainerBuilder;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.*;
import fr.treeptik.cloudunit.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.PersistenceException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class ModuleServiceImpl
        implements ModuleService {

    private Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @Inject
    private ModuleDAO moduleDAO;

    @Inject
    private ImageService imageService;

    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private UserService userService;

    @Inject
    private ServerService serverService;

    @Inject
    private EmailUtils emailUtils;

    @Inject
    private ShellUtils shellUtils;

    @Inject
    private PortUtils portUtils;

    @Inject
    private HipacheRedisUtils hipacheRedisUtils;

    @Inject
    private ContainerMapper containerMapper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private SnapshotService snapshotService;

    @Value("${suffix.cloudunit.io}")
    private String suffixCloudUnitIO;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${env.exec}")
    private String envExec;

    @Value("${cloudunit.instance.name}")
    private String cuInstanceName;

    @Value("${ip.for.registry}")
    private String registryIp;

    @Value("${database.hostname}")
    private String databaseHostname;

    public ModuleDAO getModuleDAO() {
        return this.moduleDAO;
    }

    /**
     * comprend deux étapes : Creation et affectation des valeurs des proprietes
     * du container et du service hébergé / Ajout des variables d'environnement
     * aux serveurs associés
     */
    @Override
    public Module initModule(Application application, Module module,
                             String tagName)
            throws ServiceException, CheckException {
        this.createAndAffectModuleValues(application, module, tagName);
        this.connectModuleToServers(application, module, tagName);
        return module;
    }

    /**
     * Save app in just in DB, not create container use principally to charge
     * status.PENDING of entity until it's really functionnal
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public Module saveInDB(Module module)
            throws ServiceException {
        moduleDAO.saveAndFlush(module);
        return module;
    }

    @Transactional(rollbackFor = ServiceException.class)
    private Module connectModuleToServers(Application application,
                                          Module module, String tagName)
            throws ServiceException,
            CheckException {

        if (module.getImage().getImageType().equals("module")) {
            Map<String, String> configShell = new HashMap<>();
            for (Server server : application.getServers()) {

                String command = null;
                try {
                    // On redémarre temporairement le container Server et
                    // lancer les scripts via SSH
                    if (module.getApplication().getStatus().equals(Status.STOP)) {
                        serverService.startServer(server);
                        application.setStatus(Status.STOP);
                        application = applicationDAO.saveAndFlush(application);
                    }

                    logger.info("dockerManagerAddress="
                            + application.getManagerIp());
                    logger.info("password="
                            + server.getApplication().getUser().getPassword());
                    logger.info("port=" + server.getSshPort());

                    configShell.put("password", server.getApplication()
                            .getUser().getPassword());
                    configShell.put("port", server.getSshPort());
                    configShell.put("dockerManagerAddress",
                            application.getManagerIp());

                    int counter = 0;
                    while (!server.getStatus().equals(Status.START)) {
                        if (counter == 100) {
                            break;
                        }
                        Thread.sleep(1000);
                        logger.info(" wait server sshd processus start");
                        logger.info("SSHDSTATUS = server : "
                                + server.getStatus());
                        server = serverService.findById(server.getId());
                        counter++;
                    }
                    if (tagName == null) {
                        Thread.sleep(3000);
                        command = "sh /cloudunit/scripts/addDBEnvVar.sh "
                                + module.getModuleInfos().get("username")
                                + " "
                                + module.getModuleInfos().get("password")
                                + " "
                                + module.getInternalDNSName()
                                + " "
                                + module.getImage()
                                .getName()
                                .substring(
                                        0,
                                        module.getImage().getName()
                                                .indexOf("-"))
                                .toUpperCase() + "_"
                                + module.getInstanceNumber();
                    } else {
                        Thread.sleep(3000);
                        Snapshot snapshot = snapshotService.findOne(tagName);
                        Map<String, String> map = new HashMap<>();

                        for (String key : snapshot.getAppConfig().keySet()) {
                            if (snapshot
                                    .getAppConfig()
                                    .get(key)
                                    .getProperties()
                                    .get("username-"
                                            + module.getImage().getName()) != null) {
                                map.put("username",
                                        snapshot.getAppConfig()
                                                .get(key)
                                                .getProperties()
                                                .get("username-"
                                                        + module.getImage()
                                                        .getName()));
                            }

                            if (snapshot
                                    .getAppConfig()
                                    .get(key)
                                    .getProperties()
                                    .get("password-"
                                            + module.getImage().getName()) != null) {
                                map.put("password",
                                        snapshot.getAppConfig()
                                                .get(key)
                                                .getProperties()
                                                .get("password-"
                                                        + module.getImage()
                                                        .getName()));

                            }
                        }

                        command = "sh /cloudunit/scripts/addDBEnvVarForClone.sh "
                                + map.get("username")
                                + " "
                                + map.get("password")
                                + " "
                                + module.getInternalDNSName()
                                + " "
                                + module.getImage()
                                .getName()
                                .substring(
                                        0,
                                        module.getImage().getName()
                                                .indexOf("-"))
                                .toUpperCase()
                                + "_"
                                + module.getInstanceNumber();
                    }
                    logger.info("command shell to execute [" + command + "]");

                    shellUtils.executeShell(command, configShell);

                    if (module.getApplication().getStatus().equals(Status.STOP)) {
                        this.stopModule(module);
                        serverService.stopServer(server);
                        application.setStatus(Status.STOP);
                    }
                } catch (Exception e1) {
                    server.setStatus(Status.FAIL);
                    serverService.saveInDB(server);
                    module.setStatus(Status.FAIL);
                    this.saveInDB(module);
                    logger.error("Error :  Error during adding module " + e1);
                    e1.printStackTrace();

                    throw new ServiceException(e1.getLocalizedMessage(), e1);
                }
            }
            this.update(module);
            this.sendEmail(module);
        }

        return module;
    }

    @Transactional
    private Module createAndAffectModuleValues(Application application,
                                               Module module, String tagName)
            throws ServiceException,
            CheckException {

        try {
            // user = userService.findById(user.getId());

            module.setImage(imageService
                    .findByName(module.getImage().getName()));

            // Create container module in docker
            module = this.create(application, module, tagName);

            Long instanceNumber = module.getInstanceNumber();

            // Add extra properties
            module.setStartDate(new Date());
            module.setInternalDNSName(module.getName() + "." + module.getImage().getName() + ".cloud.unit");

            this.addModuleManager(module, instanceNumber);

            // save module in DB
            module = this.update(module);

        } catch (ServiceException e) {
            module.setStatus(Status.FAIL);
            logger.error(application.toString() + module.toString(), e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return module;
    }

    /**
     * Create module in docker (Not in DB)
     *
     * @param module
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @Transactional
    private Module create(Application application, Module module, String tagName)
            throws ServiceException, CheckException {

        logger.debug("create : Methods parameters : " + module);
        logger.info("logger.ModuleService : Starting creating Module "
                + module.getName());

        // Nommage du module - Méthode recursive, on part de 1
        module = initNewModule(module, application.getName(), 1);
        DockerContainer dataDockerContainer = new DockerContainer();

        if (module.getImage().getImageType().equals("module")
                & !module.getName().contains("git")) {
            dataDockerContainer = this.createAndStartDataContainer(module,
                    module.getName(), tagName);
        }

        String imagePath = module.getImage().getPath();
        if (logger.isDebugEnabled()) {
            logger.info("imagePath:" + imagePath);
        }

        // si le tag n'est pas nul on récupère la bonne image pour git

        if (tagName != null & module.getImage().getName().contains("git")) {
            imagePath = registryIp+":5000/cloudunit/git"
                    + tagName.replace(":", "") + tagName;
        }

        DockerContainer dockerContainer = new DockerContainer();

        // Définition des paramètres Docker du container
        dockerContainer = DockerContainerBuilder.dockerContainer()
                .withName(module.getName()).withImage(imagePath).withMemory(0L)
                .withMemorySwap(0L).build();

        module.getModuleAction().initModuleInfos();

        if (tagName != null & !module.getImage().getName().contains("git")) {
            List<String> commandesSpe = new ArrayList<>();

            Snapshot snapshot = snapshotService.findOne(tagName);
            Map<String, String> map = new HashMap<>();

            for (String key : snapshot.getAppConfig().keySet()) {
                if (key.equalsIgnoreCase(module.getImage().getPath() + "-"
                        + module.getInstanceNumber() + "-data")) {

                    if (logger.isDebugEnabled()) {
                        logger.debug("KEY : " + key);
                        logger.debug("MODULE : " + module.getImage().getPath() + "-" + module.getInstanceNumber() + "-data");
                    }

                    map.put("username",
                            snapshot.getAppConfig()
                                    .get(key)
                                    .getProperties()
                                    .get("username-" + module.getImage().getName()));
                    map.put("password",
                            snapshot.getAppConfig()
                                    .get(key)
                                    .getProperties()
                                    .get("password-"
                                            + module.getImage().getName()));
                }

            }

            commandesSpe.addAll(module.getModuleAction().createDockerCmdForClone(map, databasePassword, envExec, databaseHostname));
            dockerContainer.setCmd(commandesSpe);
        } else {
            dockerContainer.setCmd(module.getModuleAction().createDockerCmd(databasePassword, envExec, databaseHostname));
        }

        try {
            DockerContainer.create(dockerContainer,
                    application.getManagerIp());
            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());

            if (module.getImage().getImageType().equals("module")) {
                List<String> volumesFrom = new ArrayList<>();
                volumesFrom.add(dataDockerContainer.getName());
                volumesFrom.add("java");
                dockerContainer.setVolumesFrom(volumesFrom);
            }

            if (module.getImage().getName().contains("git")) {
                dockerContainer.setPortBindings("22/tcp", "0.0.0.0",
                        module.getSshPort());
                dockerContainer.setVolumesFrom(Arrays.asList("java"));
            }

            dockerContainer = DockerContainer.start(dockerContainer,
                    application.getManagerIp());

            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());

            module = containerMapper.mapDockerContainerToModule(
                    dockerContainer, module);

            if (module.getApplication().getStatus().equals(Status.STOP)) {

                DockerContainer.stop(dockerContainer,
                        application.getManagerIp());
                module.setStatus(Status.STOP);
            }

            if (module.getApplication().getStatus().equals(Status.START) ||
                    module.getApplication().getStatus().equals(Status.PENDING)) {
                module.setStatus(Status.START);
            }

            module = this.update(module);

        } catch (DockerJSONException e) {
            module.setStatus(Status.FAIL);
            this.saveInDB(module);
            logger.error("ModuleService Error : Create Module " + e);
            throw new ServiceException("Error docker : "
                    + e.getLocalizedMessage(), e);
        }
        logger.info("ModuleService : Module " + module.getName()
                + " successfully created.");
        return module;
    }

    /**
     * Creation d'un container contenant uniquement les données liées au
     * container mysql
     *
     * @param module
     * @param containerName
     * @param tagName
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @Transactional
    private DockerContainer createAndStartDataContainer(Module module,
                                                        String containerName, String tagName)
            throws ServiceException,
            CheckException {

        String imagePath = module.getImage().getPath() + "-data";

        if (tagName != null) {
            imagePath = registryIp+":5000/"
                    + imagePath.replaceAll("-data",
                    "-" + module.getInstanceNumber() + "-data")
                    + tagName + ":" + tagName;
        }

        Application application = module.getApplication();

        String dataContainerName = containerName + "-data";

        logger.debug("create : Methods parameters : " + module + " -- "
                + application + " -- containerName = " + containerName);
        logger.info("logger.ModuleService : Starting creating data container for "
                + module.getName());

        Map<String, String> ports = new HashMap<String, String>();
        logger.debug("imagePath:" + imagePath);
        DockerContainer dataContainer = DockerContainerBuilder
                .dockerContainer()
                .withName(dataContainerName)
                .withImage(imagePath)
                .withMemory(0L)
                .withMemorySwap(0L)
                .withPorts(ports)
                .withCmd(
                        Arrays.asList("/bin/sh",
                                "/cloudunit/scripts/start-service.sh",
                                module.getApplication().getUser().getLogin(),
                                module.getApplication().getUser().getPassword()))
                .build();

        try {

            DockerContainer.create(dataContainer, application.getManagerIp());
            dataContainer = DockerContainer.findOne(dataContainer,
                    application.getManagerIp());

            /**
             * Création du volume entre l'hôte et le dossier de destination du
             * tar de /cloudunit/database un crontab fera un tar toutes les
             * heures de /cloudunit/database vers /cloudunit/backup ce tar sera
             * accessible directement sur l'hôte même si le container est
             * supprimé.
             */

            DockerContainer.start(dataContainer, application.getManagerIp());

            dataContainer = DockerContainer.findOne(dataContainer,
                    application.getManagerIp());

            if (tagName != null) {
                this.restoreDataModule(module);
            }

        } catch (DockerJSONException e) {
            module.setStatus(Status.FAIL);
            module = this.saveInDB(module);
            logger.error("ModuleService Error : Create data container" + e);
            throw new ServiceException("Error docker : "
                    + e.getLocalizedMessage(), e);
        }
        return dataContainer;
    }

    private void sendEmail(Module module)
            throws ServiceException {

        Map<String, Object> mapConfigEmail = new HashMap<>();

        mapConfigEmail.put("module", module);
        mapConfigEmail
                .put("user",
                        userService.findById(module.getApplication().getUser()
                                .getId()));
        mapConfigEmail.put("emailType", "moduleInformations");

        try {
            emailUtils.sendEmail(mapConfigEmail);
        } catch (MessagingException e) {
            logger.error("Error while sending email " + e);
            // On ne bloque pas l'appli pour une erreur d'email
            // Les infos sont aussi dans le CLI
        }
    }

    /**
     * check if the status passed in parameter is the as in db if it's case a
     * checkException is throws
     *
     * @throws ServiceException CheckException
     */
    @Override
    public void checkStatus(Module module, String status)
            throws CheckException, ServiceException {
        logger.info("--CHECK APP STATUS--");

        if (module.getStatus().name().equalsIgnoreCase(status)) {
            if (module.getStatus().name().equalsIgnoreCase(status)) {
                throw new CheckException("Error : Module " + module.getName()
                        + " is already " + status + "ED");
            }
        }
    }

    /**
     * check if the status is PENDING return TRUE else return false
     *
     * @throws ServiceException CheckException
     */
    @Override
    public boolean checkStatusPENDING(Module module)
            throws ServiceException {
        logger.info("--CHECK MODULE STATUS PENDING--");
        if (module.getStatus().name().equalsIgnoreCase("PENDING")) {
            return true;
        } else {
            return false;
        }
    }

    public void checkImageExist(String moduleName)
            throws ServiceException {
        try {
            imageService.findByName(moduleName);
        } catch (ServiceException e) {
            throw new ServiceException("Error : the module " + moduleName
                    + " is not available", e);
        }
    }

    @Override
    @Transactional
    public Module update(Module module)
            throws ServiceException {

        logger.debug("update : Methods parameters : " + module.toString());
        logger.info("ModuleService : Starting updating Module "
                + module.getName());
        try {
            module = moduleDAO.save(module);
        } catch (PersistenceException e) {
            module.setStatus(Status.FAIL);
            module = this.saveInDB(module);
            logger.error("ModuleService Error : update Module" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        logger.info("ModuleService : Module " + module.getName()
                + " successfully updated.");
        return module;
    }

    @Override
    @Transactional
    public Module remove(Application application, User user, Module module,
                         Boolean isModuleRemoving, Status previousApplicationStatus)
            throws ServiceException, CheckException {

        logger.debug("remove : Methods parameters : " + module.getName()
                + " applicationName " + application.getName());

        try {

            logger.info("Module to remove : " + module);
            logger.info("From application : " + application);

            // Unsubscribe module manager
            module.getModuleAction()
                    .unsubscribeModuleManager(hipacheRedisUtils);

            // Delete container in docker
            DockerContainer dockerContainer = new DockerContainer();
            dockerContainer.setName(module.getName());
            dockerContainer.setImage(module.getImage().getName());

            String imageId = "";
            if (module.getName().contains("git")) {
                imageId = DockerContainer.findOneWithImageID(dockerContainer,
                        application.getManagerIp()).getImageID();
            } else {
                DockerContainer dataContainer = new DockerContainer();
                dataContainer.setName(dockerContainer.getName() + "-data");
                imageId = DockerContainer.findOneWithImageID(dataContainer,
                        application.getManagerIp()).getImageID();
            }

            if (module.getStatus().equals(Status.START)) {
                DockerContainer.stop(dockerContainer, application.getManagerIp());
            }
            DockerContainer.remove(dockerContainer, application.getManagerIp());

            try {
                if (application.isAClone()) {
                    DockerContainer.deleteImage(imageId,
                            application.getManagerIp());
                }
            } catch (DockerJSONException e) {
                logger.info("Others apps use this docker images");
            }

            // Delete in database
            if (isModuleRemoving) {

                for (Server server : application.getServers()) {
                    Map<String, String> configShell = new HashMap<>();

                    // On redémarre temporairement les containers Server et
                    // Module pour lancer les scripts via SSH
                    if (previousApplicationStatus.equals(Status.STOP)) {
                        serverService.startServer(server);
                        application.setStatus(Status.STOP);
                        application = applicationDAO.save(application);

                    }

                    configShell.put("port", server.getSshPort());
                    configShell.put("dockerManagerAddress",
                            application.getManagerIp());
                    configShell.put("password", server.getApplication().getUser().getPassword());

                    String command;
                    Integer exitCode1;
                    command = "sh /cloudunit/scripts/rmDBEnvVar.sh "
                            + module.getInstanceNumber();

                    int counter = 0;
                    while (!server.getStatus().equals(Status.START)) {
                        if (counter == 100) {
                            break;
                        }
                        Thread.sleep(1000);
                        logger.info(" wait server sshd processus start");
                        logger.info("SSHDSTATUS = server : "
                                + server.getStatus());
                        server = serverService.findById(server.getId());
                        counter++;
                    }

                    logger.info("command shell to execute [" + command + "]");
                    exitCode1 = shellUtils.executeShell(command, configShell
                    );

                    if (exitCode1 != 0) {
                        server.setStatus(Status.FAIL);
                        serverService.saveInDB(server);
                        logger.error("Error : Error during reset module's parameters of server - exitCode1 = "
                                + exitCode1);
                        throw new ServiceException(
                                "Error : Error during reset module's parameters of server - exitCode1 = "
                                        + exitCode1, null);
                    }

                    if (previousApplicationStatus.equals(Status.STOP)) {
                        serverService.stopServer(server);
                        application.setStatus(Status.STOP);
                    }
                }
            }

            moduleDAO.delete(module);

            logger.info("ModuleService : Module successfully removed ");

        } catch (PersistenceException e) {
            module.setStatus(Status.FAIL);
            throw new ServiceException("Error database : failed to remove " + module.getName(), e);
        } catch (DockerJSONException e) {
            throw new ServiceException("Error docker : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            //todo
            logger.error(e.getMessage());
        }

        return module;
    }

    @Override
    @Transactional
    public Module startModule(Module module)
            throws ServiceException {

        logger.debug("start : Methods parameters : " + module);
        logger.info("Module : Starting module " + module.getName());

        Map<String, String> forwardedPorts = new HashMap<>();

        Application application = module.getApplication();

        try {
            DockerContainer dockerContainer = new DockerContainer();
            DockerContainer dataDockerContainer = new DockerContainer();
            dockerContainer.setName(module.getName());
            dockerContainer.setPorts(forwardedPorts);
            dockerContainer.setImage(module.getImage().getName());

            if (module.getImage().getImageType().equals("module")) {
                dockerContainer.setVolumesFrom(module.getVolumesFrom());
            }
            if (module.getImage().getName().contains("git")) {
                dockerContainer.setPortBindings("22/tcp", "0.0.0.0",
                        module.getSshPort());
            }

            DockerContainer
                    .start(dockerContainer, application.getManagerIp());
            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());

            if (module.getImage().getImageType().equals("module")) {
                dataDockerContainer.setName(module.getName() + "-data");

                DockerContainer.start(dataDockerContainer,
                        application.getManagerIp());
            }

            module = containerMapper.mapDockerContainerToModule(
                    dockerContainer, module);

            // Unsubscribe module manager
            module.getModuleAction()
                    .updateModuleManager(hipacheRedisUtils);

        } catch (PersistenceException e) {
            module.setStatus(Status.FAIL);
            module = this.saveInDB(module);
            logger.error("ModuleService Error : fail to start Module" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        } catch (DockerJSONException e) {
            module.setStatus(Status.FAIL);
            module = this.saveInDB(module);
            logger.error("ModuleService Error : fail to start Module " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return module;
    }

    @Override
    @Transactional
    public Module stopModule(Module module)
            throws ServiceException {

        try {
            Application application = module.getApplication();

            DockerContainer dockerContainer = new DockerContainer();
            dockerContainer.setName(module.getName());
            dockerContainer.setImage(module.getImage().getName());
            DockerContainer.stop(dockerContainer, application.getManagerIp());
            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());

            module.setDockerState(dockerContainer.getState());
            module.setStatus(Status.STOP);
            module = this.update(module);

        } catch (DataAccessException e) {
            module.setStatus(Status.FAIL);
            module = this.saveInDB(module);
            throw new ServiceException(e.getLocalizedMessage(), e);
        } catch (DockerJSONException e) {
            module.setStatus(Status.FAIL);
            module = this.saveInDB(module);
            logger.error("Fail to stop Module" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return module;
    }

    @Override
    public Module findById(Integer id)
            throws ServiceException {
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
    public List<Module> findAll()
            throws ServiceException {
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
    public List<Module> findAllStatusStartModules()
            throws ServiceException {
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
    public List<Module> findAllStatusStopModules()
            throws ServiceException {
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
    public Module findByContainerID(String id)
            throws ServiceException {
        try {
            return moduleDAO.findByContainerID(id);
        } catch (PersistenceException e) {
            logger.error("Error ModuleService : error findCloudId Method : "
                    + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Module findByName(String moduleName)
            throws ServiceException {
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
    public List<Module> findByApp(Application application)
            throws ServiceException {
        try {
            return moduleDAO.findByApp(application.getName(), cuInstanceName);
        } catch (PersistenceException e) {
            logger.error("Error ModuleService : error findByApp Method : " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<Module> findByAppAndUser(User user, String applicationName)
            throws ServiceException {
        try {
            List<Module> modules = moduleDAO.findByAppAndUser(user.getId(), applicationName, cuInstanceName);
            return modules;
        } catch (PersistenceException e) {
            logger.error("Error ModuleService : error findByAppAndUser Method : "
                    + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

    }

    @Override
    @Transactional
    public void addModuleManager(Module module, Long instanceNumber)
            throws ServiceException {
        try {
            module = module.getModuleAction().enableModuleManager(hipacheRedisUtils, module, instanceNumber);

            String subdomain = System.getenv("CU_SUB_DOMAIN") == null ? "" : System.getenv("CU_SUB_DOMAIN");
            module.setManagerLocation(module.getModuleAction().getManagerLocation(subdomain, suffixCloudUnitIO));

            // persist in database
            update(module);

        } catch (ServiceException e) {
            logger.error("Error ModuleService : error addModuleManager Method : "
                    + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

    }

    public Module restoreBackup(String moduleName)
            throws ServiceException {

        Module module = this.findByName(moduleName);

        logger.debug("restoreBackup  of module : : " + module);

        String scriptHostPath = "/vagrant_cloudunit/cu-services/scriptHost/";
        String dataContainerName = module.getName() + "-data";
        logger.info("logger.ModuleService : Starting finding data container for "
                + dataContainerName);

        Application application = module.getApplication();

        DockerContainer dataContainer = new DockerContainer();
        dataContainer.setName(dataContainerName);
        try {
            dataContainer = DockerContainer.findOne(dataContainer,
                    application.getManagerIp());
        } catch (DockerJSONException e) {
            module.setStatus(Status.FAIL);
            this.saveInDB(module);
            logger.error("ModuleService Error : Create Module" + e);
            throw new ServiceException("Error docker : "
                    + e.getLocalizedMessage(), e);
        }

        Map<String, String> configShell = new HashMap<>();
        configShell.put("port", "22");
        configShell.put("dockerManagerAddress", application.getManagerIp());
        String command;

        try {
            command = "expect " + scriptHostPath + "restoreBackupExpect.sh "
                    + module.getName() + " "
                    + module.getModuleInfos().get("username") + " "
                    + module.getModuleInfos().get("password") + " "
                    + module.getApplication().getName() + " "
                    + module.getApplication().getUser().getPassword();
            shellUtils.executeShell(command, configShell);

            logger.info(command);

            DockerContainer dockerContainer = new DockerContainer();
            dockerContainer.setName(module.getName());
            dockerContainer.setImage(module.getImage().getName());
            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());

            module = containerMapper.mapDockerContainerToModule(
                    dockerContainer, module);

            this.update(module);

            hipacheRedisUtils.updatedAdminAddress(application,
                    application.getManagerIp(),
                    module.getListPorts().get("80/tcp"), module.getImage()
                            .getManagerName(),
                    Long.parseLong(module.getName()
                            .substring(module.getName().lastIndexOf("-")
                                    + 1)));

        } catch (DockerJSONException e) {
            application.setStatus(Status.FAIL);

            logger.error("ServerService Error : fail to start server" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return module;

    }

    @Override
    @Transactional
    public void initDb(User user, String applicationName,
                       final String moduleName, File file)
            throws ServiceException {

        logger.info("initData : " + applicationName + " - " + moduleName);

        Map<String, String> configShell = new HashMap<>();

        Application application = applicationDAO.findByNameAndUser(
                user.getId(), applicationName, cuInstanceName);

        if (application == null) {
            throw new ServiceException("initData : Application not found", null);
        }

        logger.debug("Application found ID " + application.getId() + " - "
                + application.getName());

        Collection<Module> modules = Collections2.filter(
                application.getModules(), new Predicate<Module>() {
                    @Override
                    public boolean apply(Module input) {
                        return input.getName().equalsIgnoreCase(moduleName) ? true
                                : false;
                    }
                });

        if (modules.size() < 1) {
            throw new ServiceException("initDb : Module not found", null);
        }

        Module module = modules.iterator().next();

        logger.debug("Module found " + module.getId() + " - "
                + module.getName());

        try {

            configShell.put("port", module.getSshPort());
            configShell.put("dockerManagerAddress",
                    application.getManagerIp());
            String rootPassword = module.getApplication().getUser()
                    .getPassword();
            configShell.put("password", rootPassword);

            shellUtils.sendFile(file, "root", rootPassword,
                    module.getSshPort(), application.getManagerIp(),
                    "/cloudunit/software/tmp/initData.sql");

            shellUtils.executeShell(module.getModuleAction().getInitDataCmd(),
                    configShell);

            module.setStatus(Status.START);
            module = this.update(module);

            // file.delete();

        } catch (Exception e) {

            module.setStatus(Status.FAIL);
            this.saveInDB(module);

            throw new ServiceException(e.getLocalizedMessage(), e);
        }

    }

    /**
     * Affecte un nom disponible pour le module que l'on souhaite créer
     *
     * @param module
     * @param applicationName
     * @param counter
     * @return module
     * @throws ServiceException
     */
    private Module initNewModule(Module module, String applicationName,
                                 int counter)
            throws ServiceException {
        try {

            Long nbInstance = imageService.countNumberOfInstances(
                    module.getName(), applicationName, module.getApplication()
                            .getUser().getLogin(), cuInstanceName);

            Long counterGlobal = (nbInstance.longValue() == 0 ? 1L
                    : (nbInstance + counter));
            try {
                String containerName = AlphaNumericsCharactersCheckUtils
                        .convertToAlphaNumerics(cuInstanceName.toLowerCase()) + "-" + AlphaNumericsCharactersCheckUtils
                        .convertToAlphaNumerics(module.getApplication()
                                .getUser().getLogin())
                        + "-"
                        + AlphaNumericsCharactersCheckUtils
                        .convertToAlphaNumerics(module.getApplication()
                                .getName())
                        + "-"
                        + module.getName()
                        + "-" + counterGlobal;
                logger.info("containerName generated : " + containerName);
                Module moduleTemp = moduleDAO.findByName(containerName);
                if (moduleTemp == null) {
                    module.setName(containerName);
                } else {
                    initNewModule(module, applicationName, counter + 1);
                }
            } catch (UnsupportedEncodingException e1) {
                throw new ServiceException("Error renaming container", e1);
            }
        } catch (ServiceException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return module;
    }

    @Override
    public Module findGitModule(String login, Application application)
            throws ServiceException {
        try {
            return moduleDAO.findGitModule(login, application.getName(), cuInstanceName);
        } catch (PersistenceException e) {
            logger.error("Error ModuleService : error find git container Method : "
                    + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    private void restoreDataModule(Module module) {
        Application application;
        try {
            Thread.sleep(5000);
            application = applicationService.findByNameAndUser(module
                            .getApplication().getUser(),
                    module.getApplication()
                            .getName());
            DockerContainer dockerContainer = new DockerContainer();
            dockerContainer.setName(module.getName() + "-data");
            dockerContainer = DockerContainer.findOne(dockerContainer,
                    application.getManagerIp());
            Map<String, String> configShell = new HashMap<>();
            configShell.put("port", dockerContainer.getPorts().get("22/tcp"));
            configShell.put("dockerManagerAddress",
                    application.getManagerIp());
            String rootPassword = module.getApplication().getUser()
                    .getPassword();
            configShell.put("password", rootPassword);
            int code = shellUtils.executeShell(
                    "/cloudunit/scripts/restore-data.sh", configShell);
            logger.info("The backup script return : " + code);

        } catch (ServiceException | CheckException | DockerJSONException
                | InterruptedException e) {
            logger.error("" + module, e);
        }
    }

}

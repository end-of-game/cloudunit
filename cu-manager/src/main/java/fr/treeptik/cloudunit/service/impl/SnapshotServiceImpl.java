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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import fr.treeptik.cloudunit.dao.ModuleConfigurationDAO;
import fr.treeptik.cloudunit.dao.SnapshotDAO;
import fr.treeptik.cloudunit.docker.DockerContainerJSON;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.dto.LogLine;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
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
import fr.treeptik.cloudunit.service.ImageService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.service.SnapshotService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.ShellUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Service
public class SnapshotServiceImpl
        implements SnapshotService {

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
    private ImageService imageService;

    @Inject
    private ShellUtils shellUtils;

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
        if (tag != null) { tag = tag.toLowerCase(); }

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
            snapshot.setDate(new Date());
            snapshot.setTag(tag);
            snapshot.setFullTag(user.getLogin()+"-"+tag);
            snapshot.setCuInstanceName(cuInstanceName);
            snapshot.setDescription(description);
            snapshot.setUser(application.getUser());
            snapshot.setDeploymentStatus(application.getDeploymentStatus());

            snapshot.setSavedPorts(
                    application.getPortsToOpen()
                            .stream()
                            .map(c -> c.getPort() + ";" + c.getNature())
                            .collect(Collectors.toList()));

            Map<String, ModuleConfiguration> config = new HashMap<>();
            for (Server server : application.getServers()) {
                snapshot = server.getServerAction().cloneProperties(snapshot);
            }
            for (Module module : application.getModules()) {

                if (!module.getImage().getPath().contains("git")) {

                    ModuleConfiguration moduleConfiguration =
                            moduleConfigurationDAO.saveAndFlush(module.getModuleAction().cloneProperties());
                    config.put(moduleConfiguration.getPath(), moduleConfiguration);
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
                DockerContainer.commit(dockerContainer,
                                        snapshot.getFullTag(),
                                        application.getManagerIp(),
                                        server.getImage().getPath());
            }

            for (Module module : application.getModules()) {
                String imageName = "";
                String moduleName = "";
                if (module.getImage().getPath().contains("git")) {
                    moduleName = module.getName();
                    imageName = module.getImage().getPath();
                } else {
                    moduleName = module.getName() + "-data";
                    imageName = module.getImage().getPath() + "-" + module.getInstanceNumber() + "-data";
                    this.backupModule(module);
                }
                images.add(imageName);
                DockerContainer dockerContainer = new DockerContainer();
                dockerContainer.setName(moduleName);
                dockerContainer.setImage(module.getImage().getName());
                DockerContainer.commit(dockerContainer,
                                        snapshot.getFullTag(),
                                        application.getManagerIp(), imageName);
            }

            snapshot.setImages(images);
            snapshot = snapshotDAO.save(snapshot);

        } catch (DockerJSONException | InterruptedException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        return snapshot;
    }

    public Snapshot findByTagAndUser(String login, String tag) {
        if (tag != null) { tag = tag.toLowerCase(); }
        return snapshotDAO.findByTag(login+"-"+tag);
    }

    @Override
    public List<Snapshot> listAll()
            throws ServiceException {
        try {
            return snapshotDAO.listAll();
        } catch (DataAccessException e) {
            throw new ServiceException("Error : " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    @Transactional
    public Snapshot remove(String tag)
            throws ServiceException, CheckException {
        Snapshot snapshot = null;
        if (tag != null) { tag = tag.toLowerCase(); }
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            snapshot = snapshotDAO.findByTag(tag);

            if (snapshot == null) {
                throw new CheckException("Error : this snapshot doesn't exist : " + tag);
            }

            List<String> images = snapshotDAO.findAllImagesFromASnapshot(tag).getImages();

            for (String image : images) {
                DockerContainer dockerContainer = new DockerContainer();
                dockerContainer.setImage(image);
                DockerContainer.deleteImage(image+":"+tag, dockerManagerIp);
            }

            snapshotDAO.delete(snapshotDAO.findByTag(tag));

        } catch (DockerJSONException | DataAccessException e) {
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

            if (applicationService.countApp(user) >= Integer.parseInt(numberMaxApplications)) {
                authentificationUtils.allowUser(user);
                throw new ServiceException("You have already created your " + numberMaxApplications
                        + " apps into the Cloud");
            }
            if (applicationService.checkAppExist(user, applicationName)) {
                authentificationUtils.allowUser(user);
                throw new CheckException("This application already exists");
            }

            if (!tagExists(tag, user.getLogin())) {
                throw new CheckException("This tag does not exist yet");
            }

            // creation de la nouvelle app à partir de l'image tagée
            Application application =
                    applicationService.create(applicationName, user.getLogin(), snapshot.getType(),
                            snapshot.getFullTag(), snapshot.getTag());

            // We need it to get lazy modules relationships
            application = applicationService.findByNameAndUser(application.getUser(), application.getName());

            Module moduleGit = moduleService.findGitModule(user.getLogin(), application);

            for (Server server : application.getServers()) {
                while (!server.getStatus().equals(Status.START) || !moduleGit.getStatus().equals(Status.START)) {
                    Thread.sleep(500);
                    logger.info(" wait git and server sshd processus start");
                    logger.info("SSHDSTATUS = server : " + server.getStatus() + " - module : " + moduleGit.getStatus());
                    moduleGit = moduleService.findById(moduleGit.getId());
                    server = serverService.findById(server.getId());
                }
                serverService.update(server, snapshot.getJvmMemory().toString(), snapshot.getJvmOptions(),
                        snapshot.getJvmRelease(), false);
            }

            restoreModule(snapshot, application, tag);

            application.setDeploymentStatus(snapshot.getDeploymentStatus());
            applicationService.saveInDB(application);

            for (String savedPort : snapshot.getSavedPorts()) {
                applicationService.addPort(application,
                        savedPort.split(";")[1],
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
        try {
            DockerClient docker = null;
            if (Boolean.valueOf(isHttpMode)) {
                docker = DefaultDockerClient
                        .builder()
                        .uri("http://" + dockerManagerIp).build();
            } else {
                final DockerCertificates certs = new DockerCertificates(Paths.get(certsDirPath));
                docker = DefaultDockerClient
                        .builder()
                        .uri("https://" + dockerManagerIp).dockerCertificates(certs).build();
            }

            final String[] commandStart = {"bash", "-c", "/cloudunit/scripts/cu-start.sh"};
            final String[] commandStop = {"bash", "-c", "/cloudunit/scripts/cu-stop.sh"};
            final String[] commandBackupData = {"bash", "-c", "/cloudunit/scripts/backup-data.sh"};

            docker.execCreate(module.getName(), commandStop, DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);
            docker.execCreate(module.getName()+"-data", commandBackupData, DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);
            docker.execCreate(module.getName(), commandStart, DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);

        } catch (Exception e) {
            logger.error(e.getMessage() + ", " + module);
        }
    }

    private void restoreModule(Snapshot snapshot, Application application, String tag)
            throws ServiceException {

        for (String key : snapshot.getAppConfig().keySet()) {
            try {
                //DockerContainer.pull(key, snapshot.getUniqueTagName(), dockerManagerIp, ipForRegistry);
                Module module = ModuleFactory.getModule(snapshot.getAppConfig().get(key).getName());
                module.setApplication(application);
                moduleService.checkImageExist(snapshot.getAppConfig().get(key).getName());
                module.getImage().setName(snapshot.getAppConfig().get(key).getName());
                module.setName(snapshot.getAppConfig().get(key).getName());
                module = moduleService.initModule(application, module, snapshot.getFullTag());
                Map<String, String> properties = new HashMap<>();
                properties.put("username", snapshot.getAppConfig().get(key).getProperties().get("username-" + module.getImage().getName()));
                properties.put("password", snapshot.getAppConfig().get(key).getProperties().get("password-" + module.getImage().getName()));
                properties.put("database", snapshot.getAppConfig().get(key).getProperties().get("database-" + module.getImage().getName()));
                module.setModuleInfos(properties);
                module = moduleService.saveInDB(module);
                moduleService.stopModule(module);
                Thread.sleep(5000);
                moduleService.startModule(module);

            } catch (CheckException | InterruptedException e) {
                throw new ServiceException(e.getLocalizedMessage(), e);
            }
        }
    }

    private boolean tagExists(String tag, String login) {
        if (snapshotDAO.findByTag(login + "-" + tag) != null) {
            return true;
        }
        return false;
    }
}

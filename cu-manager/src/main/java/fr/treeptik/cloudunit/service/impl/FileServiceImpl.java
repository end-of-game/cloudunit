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

import com.spotify.docker.client.*;
import com.spotify.docker.client.messages.Container;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.dto.SourceUnit;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.filters.explorer.ExplorerFactory;
import fr.treeptik.cloudunit.filters.explorer.ExplorerFilter;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.FileService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.FilesUtils;
import fr.treeptik.cloudunit.utils.ShellUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;

/**
 * Service for file management into container
 * Created by nicolas on 20/05/15.
 */
@Service
public class FileServiceImpl
        implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ShellUtils shellUtils;

    @Inject
    private ModuleService moduleService;

    @Inject
    private ServerService serverService;

    @Value("${docker.manager.ip:192.168.50.4:4243}")
    private String dockerManagerIp;

    /**
     * Delete all resources (files and folders) for an application + container +
     * path.
     *
     * @param applicationName
     * @param containerId
     * @param path
     * @throws ServiceException
     */
    public void deleteFilesFromContainer(String applicationName,
                                         String containerId, String path)
            throws ServiceException {
        try {
            final DockerCertificates certs = new DockerCertificates(Paths.get("/usr/local/tomcat/certificats"));
            final DockerClient docker = DefaultDockerClient
                    .builder()
                    .uri("https://" + dockerManagerIp).dockerCertificates(certs).build();
            List<Container> containers = docker.listContainers();
            for (Container container : containers) {
                if (container.id().substring(0, 12).equals(containerId) == false) {
                    continue;
                }
                final String[] command = {"bash", "-c", "rm -rf " + path};
                String containerName = container.names().get(0);
                String execId = docker.execCreate(containerName, command,
                        DockerClient.ExecParameter.STDOUT,
                        DockerClient.ExecParameter.STDERR);
                final LogStream output = docker.execStart(execId);
                if (output != null) {
                    output.close();
                }
            }
        } catch (DockerException | InterruptedException | DockerCertificateException e) {
            throw new ServiceException("Error in listByContainerIdAndPath", e);
        }
    }

    /**
     * Liste les fichiers de logs du répertoire de logs
     *
     * @param containerId
     * @return
     * @throws ServiceException
     */
    public List<SourceUnit> listLogsFilesByContainer(String containerId)
            throws ServiceException {

        List<SourceUnit> files = new ArrayList<>();
        try {
            final DockerCertificates certs = new DockerCertificates(Paths.get("/usr/local/tomcat/certificats"));
            final DockerClient docker = DefaultDockerClient
                    .builder()
                    .uri("https://" + dockerManagerIp).dockerCertificates(certs).build();
            List<Container> containers = docker.listContainers();
            for (Container container : containers) {
                if (container.id().substring(0, 12).equals(containerId) == false) {
                    continue;
                }
                String logDirectory = getLogDirectory(containerId);
                // Exec command inside running container with attached STDOUT
                // and STDERR
                final String[] command = {"bash", "-c",
                        "find " + logDirectory + " -type f ! -size 0 "};
                String execId;

                String containerName = container.names().get(0);
                execId = docker.execCreate(containerName, command,
                        DockerClient.ExecParameter.STDOUT,
                        DockerClient.ExecParameter.STDERR);
                ExplorerFilter filter = ExplorerFactory.getInstance()
                        .getCustomFilter(containerName);
                final LogStream output = docker.execStart(execId);
                final String execOutput = output.readFully();
                if (execOutput != null
                        && execOutput.contains("cannot access") == false) {

                    if (logger.isDebugEnabled()) {
                        logger.debug(execOutput);
                    }

                    StringTokenizer lignes = new StringTokenizer(execOutput,
                            "\n");

                    while (lignes.hasMoreTokens()) {
                        String name = lignes.nextToken();
                        name = name.substring(name.lastIndexOf("/") + 1);
                        SourceUnit sourceUnit = new SourceUnit(name);
                        files.add(sourceUnit);
                    }
                    output.close();
                }
            }
        } catch (DockerException | InterruptedException | DockerCertificateException e) {
            throw new ServiceException("Error in listByContainerIdAndPath", e);
        }

        return files;
    }

    /**
     * List the files by Container and Path
     *
     * @param containerId
     * @param path
     * @return
     * @throws ServiceException
     */
    public List<FileUnit> listByContainerIdAndPath(String containerId,
                                                   String path)
            throws ServiceException {

        List<FileUnit> files = new ArrayList<>();
        try {
            final DockerCertificates certs = new DockerCertificates(Paths.get("/usr/local/tomcat/certificats"));
            final DockerClient docker = DefaultDockerClient
                    .builder()
                    .uri("https://" + dockerManagerIp).dockerCertificates(certs).build();
            List<Container> containers = docker.listContainers();
            for (Container container : containers) {
                // b10da42def6c1737c40b981b1692acb24e28414944b4017814713afb811ae51b
                // log.debug(container.id() + " : " + container.names());
                if (container.id().substring(0, 12).equals(containerId) == false) {
                    continue;
                }

                // Exec command inside running container with attached STDOUT
                // and STDERR
                final String[] command = {"bash", "-c", "ls -laF " + path};
                String execId;

                String containerName = container.names().get(0);
                execId = docker.execCreate(containerName, command,
                        DockerClient.ExecParameter.STDOUT,
                        DockerClient.ExecParameter.STDERR);
                ExplorerFilter filter = ExplorerFactory.getInstance().getCustomFilter(containerName);
                final LogStream output = docker.execStart(execId);
                final String execOutput = output.readFully();
                if (execOutput != null
                        && execOutput.contains("cannot access") == false) {

                    if (logger.isDebugEnabled()) {
                        logger.debug(execOutput);
                    }

                    StringTokenizer lignes = new StringTokenizer(execOutput,
                            "\n");
                    while (lignes.hasMoreTokens()) {
                        String ligne = lignes.nextToken();
                        if (logger.isDebugEnabled()) {
                            logger.debug(ligne);
                        }
                        if (ligne.startsWith("total"))
                            continue;
                        StringTokenizer fields = new StringTokenizer(ligne, " ");
                        String rights = fields.nextToken();
                        String id = fields.nextToken();
                        String user = fields.nextToken();
                        String group = fields.nextToken();
                        String size = fields.nextToken();
                        String month = fields.nextToken();
                        String day = fields.nextToken();
                        String hour = fields.nextToken();
                        String name = fields.nextToken();
                        boolean dir = false;
                        boolean exec = false;
                        if (name.endsWith("/")) {
                            dir = true;
                            name = name.substring(0, name.length() - 1);
                        } else {
                            boolean isNotAuth = FilesUtils
                                    .isNotAuthorizedExtension(name);
                            if (isNotAuth) {
                                continue;
                            }
                        }
                        if (name.endsWith("*")) {
                            exec = true;
                            name = name.substring(0, name.length() - 1);
                        }
                        StringBuilder absolutePath = new StringBuilder(128);
                        absolutePath.append(
                                path.replaceAll("__", "/")
                                        .replaceAll("//", "/")).append(name);

                        if (name.equalsIgnoreCase("."))
                            continue;
                        if (name.equalsIgnoreCase(".."))
                            continue;

                        FileUnit fileUnit = new FileUnit(name, user, day,
                                month, hour, false, dir, exec,
                                absolutePath.toString());

                        if (filter.isValid(fileUnit)) {
                            // Add test to know if resource is removable or not
                            filter.isRemovable(fileUnit);
                            // add test to know if resource is saved or not
                            // during cloning
                            filter.isSafe(fileUnit);
                            // we add the file to explorer ui
                            files.add(fileUnit);
                        }
                    }
                    if (output != null) {
                        output.close();
                    }
                }

            }
        } catch (DockerException | InterruptedException | DockerCertificateException e) {
            throw new ServiceException("Error in listByContainerIdAndPath", e);
        }

        return files;
    }

    @Override
    public void sendFileToContainer(String applicationName, String containerId,
                                    File file, String originalName, String destFile)
            throws ServiceException {

        Application application;
        try {
            application = applicationService.findByNameAndUser(
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
            shellUtils
                    .sendFile(file, rootPassword, sshPort,
                            application.getManagerIp(),
                            convertDestPathFile(destFile));
            /*
             * déporter ce script dans le container (au plus au niveau)
			 * 
			 * renommer le fichier sur place et met les droits au user
			 */

            shellUtils.executeShell("mv " + convertDestPathFile(destFile)
                            + file.getName() + " " + convertDestPathFile(destFile)
                            + originalName + " && chown "
                            + authentificationUtils.getAuthentificatedUser().getLogin()
                            + ":"
                            + authentificationUtils.getAuthentificatedUser().getLogin()
                            + " " + convertDestPathFile(destFile) + originalName,
                    configShell);
        } catch (ServiceException | CheckException e) {
            throw new ServiceException("error in send file into the container",
                    e);
        }

    }

    @Override
    public Optional<File> getFileFromContainer(String applicationName,
                                               String containerId, File file, String originalName, String destFile)
            throws ServiceException {

        Application application;
        try {
            application = applicationService.findByNameAndUser(
                    authentificationUtils.getAuthentificatedUser(),
                    applicationName);

            Map<String, String> configShell = new HashMap<>();

            String sshPort = application.getSShPortByContainerId(containerId);
            String rootPassword = application.getUser().getPassword();
            configShell.put("port", sshPort);
            configShell.put("dockerManagerAddress",
                    application.getManagerIp());
            configShell.put("password", rootPassword);

            shellUtils.downloadFile(file, rootPassword, sshPort,
                    application.getManagerIp(), convertDestPathFile(destFile)
                            + originalName);

        } catch (ServiceException | CheckException e) {
            StringBuilder msgError = new StringBuilder();
            msgError.append("applicationName=").append("=").append(applicationName);
            msgError.append(",containerId=").append("=").append(containerId);
            msgError.append(",file.toPath()=").append(file.toPath());
            msgError.append("originalName=").append(originalName);
            msgError.append("destFile=").append(destFile);
            throw new ServiceException(msgError.toString(), e);
        }

        return Optional.of(file);
    }

    ;

    private String convertDestPathFile(String pathFile) {
        return "/" + pathFile.replaceAll("__", "/") + "/";
    }

    private String getLogDirectory(String containerId)
            throws ServiceException {

        Module module = null;
        Server server = null;
        String location = null;
        try {
            module = moduleService.findByContainerID(containerId);
            server = serverService.findByContainerID(containerId);
            if (module != null) {
                location = module.getModuleAction().getLogLocation();
            }
            if (server != null) {
                location = server.getServerAction().getLogLocation();
            }

        } catch (ServiceException e) {
            throw new ServiceException("error in send file into the container",
                    e);
        }

        return location;
    }

}

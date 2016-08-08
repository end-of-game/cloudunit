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

package fr.treeptik.cloudunit.controller;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.dto.*;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.FileService;
import fr.treeptik.cloudunit.utils.AlphaNumericsCharactersCheckUtils;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.FilesUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Locale;

/*
 * Controller for resources (files + folders) into Container.
 *
 * Created by nicolas on 20/05/15.
 */

@Controller
@RequestMapping("/file")
public class FileController {

    private static final long serialVersionUID = 1L;

    private final transient Logger logger = LoggerFactory
        .getLogger(FileController.class);

    @Inject
    private FileService fileService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private DockerService dockerService;

    private Locale locale = Locale.ENGLISH;

    /**
     * @param containerId
     * @param path
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @Deprecated
    @RequestMapping(value = "/container/{containerId}/path/{path}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<FileUnit> list(
        @PathVariable String containerId, @PathVariable String path)
        throws ServiceException, CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("path:" + path);
        }

        path = convertPathFromUI(path);
        List<FileUnit> fichiers = fileService.listByContainerIdAndPath(
            containerId, path);
        return fichiers;
    }

    /**
     * @param containerId
     * @param path
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @RequestMapping(value = "/container/{containerId}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<FileUnit> listByContainerIdAndPath(
            @PathVariable String containerId, @RequestParam("path") String path)
            throws ServiceException, CheckException {
        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("path:" + path);
        }
        path = convertPathFromUI(path);
        List<FileUnit> fichiers = fileService.listByContainerIdAndPath(containerId, path);
        return fichiers;
    }

    /**
     * Display content file from a container
     *
     * @param containerId
     * @param applicationName
     * @param request
     * @param response
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     * @returnoriginalName
     */
    @CloudUnitSecurable
    @RequestMapping(value = "/content/container/{containerId}/application/{applicationName}",
            method = RequestMethod.PUT)
    public void saveContentFileIntoContainer(
            @PathVariable final String applicationName,
            @PathVariable final String containerId,
            @RequestBody FileRequestBody fileRequestBody,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("fileName:" + fileRequestBody.getFileName());
            logger.debug("fileRequestBody: " + fileRequestBody);
        }

        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user,
                applicationName);

        // We must be sure there is no running action before starting new one
        this.authentificationUtils.canStartNewAction(user, application, locale);

        // Application is now pending
        applicationService.setStatus(application, Status.PENDING);

        if (application != null) {
            try {
                String path = convertPathFromUI(fileRequestBody.getFilePath());
                fileService.sendFileToContainer(containerId, path, null, fileRequestBody.getFileName(), fileRequestBody.getFileContent());
            } catch (ServiceException e) {
                StringBuilder msgError = new StringBuilder();
                msgError.append("containerId : " + containerId);
                msgError.append("applicationName : " + applicationName);
                msgError.append(e.getMessage());
            } finally {
                // in all case, the error during file upload cannot be critical.
                // We prefer to set the application in started mode
                applicationService.setStatus(application, Status.START);
            }
        }
    }

    /**
     * Upload a file into a container
     *
     * @return
     * @throws IOException
     * @throws ServiceException
     * @throws CheckException
     */
    @Deprecated
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}/path/{path}",
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public JsonResponse uploadFileToContainer(@PathVariable final String applicationName,
        @RequestPart("file") MultipartFile fileUpload,
        @PathVariable final String containerId,
        @PathVariable String path, HttpServletRequest request,
        HttpServletResponse response)
        throws IOException, ServiceException,
        CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug("-- CALL UPLOAD FILE TO CONTAINER FS --");
            logger.debug("applicationName = " + applicationName);
            logger.debug("containerId = " + containerId);
            logger.debug("pathFile = " + path);
        }

        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user,
            applicationName);

        // We must be sure there is no running action before starting new one
        this.authentificationUtils.canStartNewAction(user, application, locale);

        try {
            // Application is now pending
            applicationService.setStatus(application, Status.PENDING);

            fileService.sendFileToContainer(containerId, path, fileUpload, null, null);
        } finally {
            // Application is always set to start
            applicationService.setStatus(application, Status.START);
        }

        return new HttpOk();
    }

    /**
     * Upload a file into a container
     *
     * @return
     * @throws IOException
     * @throws ServiceException
     * @throws CheckException
     */
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}",
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public JsonResponse uploadFile(@PathVariable final String applicationName,
                                              @RequestPart("file") MultipartFile fileUpload,
                                              @PathVariable final String containerId,
                                              @RequestParam("path") String path, HttpServletRequest request,
                                              HttpServletResponse response)
            throws IOException, ServiceException,
            CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug("-- CALL UPLOAD FILE TO CONTAINER FS --");
            logger.debug("applicationName = " + applicationName);
            logger.debug("containerId = " + containerId);
            logger.debug("pathFile = " + path);
        }

        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user,
                applicationName);

        // We must be sure there is no running action before starting new one
        this.authentificationUtils.canStartNewAction(user, application, locale);

        try {
            // Application is now pending
            applicationService.setStatus(application, Status.PENDING);

            fileService.sendFileToContainer(containerId, path, fileUpload, null, null);
        } finally {
            // Application is always set to start
            applicationService.setStatus(application, Status.START);
        }

        return new HttpOk();
    }

    /**
     * Delete resources (files and folders) into a container for a path
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @return
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     */
    @Deprecated
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}/path/{path:.*}",
        method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteResourcesIntoContainer(
        @PathVariable final String containerId,
        @PathVariable final String applicationName,
        @PathVariable String path)
        throws ServiceException, CheckException,
        IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("path:" + path);
        }

        path = convertPathFromUI(path);
        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user,
            applicationName);

        fileService
            .deleteFilesFromContainer(applicationName, containerId, path);

        return new HttpOk();
    }

    /**
     * Create resources (files and folders) into a container for a path
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @return
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     */
    @Deprecated
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}/path/{path:.*}",
            method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse createDir(
            @PathVariable final String containerId,
            @PathVariable final String applicationName,
            @PathVariable String path)
            throws ServiceException, CheckException,
            IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("path:" + path);
        }
        fileService.createDirectory(applicationName, containerId, path);
        return new HttpOk();
    }


    /**
     * Create resources (files and folders) into a container for a path
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @return
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     */
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}",
            method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse createDirectory(
            @PathVariable final String containerId,
            @PathVariable final String applicationName,
            @RequestParam("path") String path)
            throws ServiceException, CheckException,
            IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("path:" + path);
        }
        fileService.createDirectory(applicationName, containerId, path);
        return new HttpOk();
    }


    /**
     * Delete resources (files and folders) into a container for a path
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @return
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     */
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}",
            method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteResources(
            @PathVariable final String containerId,
            @PathVariable final String applicationName,
            @RequestParam("path") String path)
            throws ServiceException, CheckException,
            IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("path:" + path);
        }

        path = convertPathFromUI(path);
        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user,
                applicationName);

        fileService
                .deleteFilesFromContainer(applicationName, containerId, path);

        return new HttpOk();
    }




    /**
     * Unzip content file into Container
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @param fileName
     * @param request
     * @param response
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     * @returnoriginalName
     */
    @Deprecated
    @RequestMapping(value = "/unzip/container/{containerId}/application/{applicationName}/path/{path}/fileName/{fileName:.*}",
            method = RequestMethod.PUT)
    public void unzipFileIntoContainer(
            @PathVariable final String containerId,
            @PathVariable final String applicationName,
            @PathVariable String path, @PathVariable final String fileName,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("fileName:" + fileName);
        }

        String command =  null;
        String realPath = convertPathFromUI(path) + "/" + fileName;
        if (FileUnit.tar().test(fileName)) {
            command = "tar xvf " + realPath + " -C " + convertPathFromUI(path);
        } else if (FileUnit.zip().test(fileName)) {
            command = "unzip " + realPath + " -d " + convertPathFromUI(path);
        } else {
            throw new CheckException("Cannot decompress this file. Extension is not right : " + realPath);
        }

        logger.info(command);
        try {
            String commandExec = dockerService.execCommand(containerId, command);
            if (commandExec != null) {
                logger.debug(commandExec);
            } else {
                logger.error("No content for : " + command);
            }
        } catch (FatalDockerJSONException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Unzip content file into Container
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @param fileName
     * @param request
     * @param response
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     * @returnoriginalName
     */
    @RequestMapping(value = "/unzip/container/{containerId}/application/{applicationName}",
            method = RequestMethod.PUT)
    public void unzipFile(
            @PathVariable final String containerId,
            @PathVariable final String applicationName,
            @RequestParam("path") final String path, @RequestParam("fileName") final String fileName,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("fileName:" + fileName);
        }

        String command =  null;
        String realPath = convertPathFromUI(path) + "/" + fileName;
        if (FileUnit.tar().test(fileName)) {
            command = "tar xvf " + realPath + " -C " + convertPathFromUI(path);
        } else if (FileUnit.zip().test(fileName)) {
            command = "unzip " + realPath + " -d " + convertPathFromUI(path);
        } else {
            throw new CheckException("Cannot decompress this file. Extension is not right : " + realPath);
        }

        logger.info(command);
        try {
            String commandExec = dockerService.execCommand(containerId, command);
            if (commandExec != null) {
                logger.debug(commandExec);
            } else {
                logger.error("No content for : " + command);
            }
        } catch (FatalDockerJSONException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Display content file from a container
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @param fileName
     * @param request
     * @param response
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     * @returnoriginalName
     */
    @Deprecated
    @RequestMapping(value = "/content/container/{containerId}/application/{applicationName}/path/{path}/fileName/{fileName:.*}",
            method = RequestMethod.GET)
    public void displayContentFileFromContainer(
            @PathVariable final String applicationName,
            @PathVariable final String containerId,
            @PathVariable String path, @PathVariable final String fileName,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException {

        downloadOrEditFile(applicationName, containerId, path, fileName, request, response, true);
    }

    /**
     * Display content file from a container
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @param fileName
     * @param request
     * @param response
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     * @returnoriginalName
     */
    @RequestMapping(value = "/content/container/{containerId}/application/{applicationName}",
            method = RequestMethod.GET)
    public void displayContentFile(
            @PathVariable final String applicationName,
            @PathVariable final String containerId,
            @RequestParam("path") String path, @RequestParam("fileName") final String fileName,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException {

        downloadOrEditFile(applicationName, containerId, path, fileName, request, response, true);
    }

    /**
     * Download a file from a container
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @param fileName
     * @param request
     * @param response
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     * @returnoriginalName
     */
    @Deprecated
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}/path/{path}/fileName/{fileName:.*}",
        method = RequestMethod.GET)
    @CloudUnitSecurable
    public void downloadFileFromContainer(
            @PathVariable final String applicationName,
            @PathVariable final String containerId,
            @PathVariable String path, @PathVariable final String fileName,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException {
        downloadOrEditFile(applicationName, containerId, path, fileName, request, response, false);
    }

    /**
     * Download a file from a container
     *
     * @param containerId
     * @param applicationName
     * @param path
     * @param fileName
     * @param request
     * @param response
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     * @returnoriginalName
     */
    @RequestMapping(value = "/container/{containerId}/application/{applicationName}",
            method = RequestMethod.GET)
    @CloudUnitSecurable
    public void downloadFile(
            @PathVariable final String applicationName,
            @PathVariable final String containerId,
            @RequestParam("path") String path, @RequestParam("fileName") final String fileName,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException {
        downloadOrEditFile(applicationName, containerId, path, fileName, request, response, false);
    }

    /**
     *  Edit or Download for FileExplorer feature
     *
     * @param applicationName
     * @param containerId
     * @param path
     * @param fileName
     * @param request
     * @param response
     * @param editionMode
     * @throws ServiceException
     * @throws CheckException
     * @throws IOException
     */
    private void downloadOrEditFile(
            final String applicationName,
            final String containerId,
            String path, final String fileName,
            HttpServletRequest request, HttpServletResponse response, Boolean editionMode)
        throws ServiceException, CheckException, IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("containerId:" + containerId);
            logger.debug("applicationName:" + applicationName);
            logger.debug("fileName:" + fileName);
        }

        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        String contentDisposition = String.format("attachment; filename=%s", fileName);
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", contentDisposition);
        if (!editionMode) {
            response.setHeader("Content-Description", "File Transfer");
            response.setContentType("utf-8");
        }

        // We must be sure there is no running action before starting new one
        this.authentificationUtils.canStartNewAction(user, application, locale);
        path = convertPathFromUI(path);
        try (OutputStream stream = response.getOutputStream()) {
            fileService.getFileFromContainer(containerId, "/" + path + "/" + fileName, stream);
            stream.flush(); // commits response!
            stream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String convertPathFromUI(String path) {
        if (path != null) {
            path = path.replaceAll("____", "/");
            path = path.replaceAll("__", "/");
        }
        return path;
    }
}

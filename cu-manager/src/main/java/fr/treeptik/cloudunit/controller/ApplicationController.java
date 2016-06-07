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

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.dto.*;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.manager.ApplicationManager;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.CheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Controller about Application lifecycle Application is the main concept for CloudUnit : it composed by Server, Module
 * and Metadata
 */
@Controller
@RequestMapping("/application")
public class ApplicationController
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Inject
    private ApplicationService applicationService;

    @Inject
    private DockerService dockerService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private ApplicationManager applicationManager;

    /**
     * To verify if an application exists or not.
     *
     * @param applicationName
     * @param serverName
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @ResponseBody
    @RequestMapping(value = "/verify/{applicationName}/{serverName}", method = RequestMethod.GET)
    public JsonResponse isValid(@PathVariable String applicationName, @PathVariable String serverName)
            throws ServiceException, CheckException {

        if (logger.isInfoEnabled()) {
            logger.info("applicationName:" + applicationName);
            logger.info("serverName:" + serverName);
        }

        CheckUtils.validateInput(applicationName, "check.app.name");
        CheckUtils.validateInput(serverName, "check.server.name");

        applicationService.isValid(applicationName, serverName);

        return new HttpOk();
    }

    /**
     * CREATE AN APPLICATION
     *
     * @param input
     * @return
     * @throws ServiceException
     * @throws CheckException
     * @throws InterruptedException
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public JsonResponse createApplication(@RequestBody JsonInput input)
            throws ServiceException, CheckException, InterruptedException {

        // replace accent characters

        /* **** Check accent in server side **** */
        //String applicationName = AlphaNumericsCharactersCheckUtils.deAccent(input.getApplicationName());
        //input.setApplicationName(applicationName);


        // validate the input
        input.validateCreateApp();

        // We must be sure there is no running action before starting new one
        User user = authentificationUtils.getAuthentificatedUser();
        authentificationUtils.canStartNewAction(user, null, Locale.ENGLISH);

        applicationManager.create(input.getApplicationName(), input.getLogin(), input.getServerName());

        return new HttpOk();
    }

    /**
     * START AN APPLICATION
     *
     * @param input {applicatioName:myApp-johndoe-admin}
     * @return
     * @throws ServiceException
     * @throws CheckException
     * @throws InterruptedException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/restart", method = RequestMethod.POST)
    public JsonResponse restartApplication(@RequestBody JsonInput input)
            throws ServiceException, CheckException, InterruptedException {

        // validate the input
        input.validateStartApp();

        String applicationName = input.getApplicationName();
        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        if (application != null && application.getStatus().equals(Status.PENDING)) {
            // If application is pending do nothing
            return new HttpErrorServer("application is pending. No action allowed.");
        }

        // We must be sure there is no running action before starting new one
        authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        if (application.getStatus().equals(Status.START)) {
            applicationManager.stop(application, user);
            applicationManager.start(application, user);
        } else if (application.getStatus().equals(Status.STOP)) {
            applicationManager.start(application, user);
        }

        return new HttpOk();
    }


    /**
     * START AN APPLICATION
     *
     * @param input {applicatioName:myApp-johndoe-admin}
     * @return
     * @throws ServiceException
     * @throws CheckException
     * @throws InterruptedException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public JsonResponse startApplication(@RequestBody JsonInput input)
            throws ServiceException, CheckException, InterruptedException {

        // validate the input
        input.validateStartApp();

        String applicationName = input.getApplicationName();
        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        if (application != null && application.getStatus().equals(Status.START)) {
            // If appliction is already start, we return the status
            return new HttpErrorServer("application already started");
        }

        // We must be sure there is no running action before starting new one
        authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        applicationManager.start(application, user);

        return new HttpOk();
    }

    /**
     * STOP a running application
     *
     * @param input
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public JsonResponse stopApplication(@RequestBody JsonInput input)
            throws ServiceException, CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug(input.toString());
        }

        String name = input.getApplicationName();
        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, name);

        // We must be sure there is no running action before starting new one
        authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        // stop the application
        applicationManager.stop(application, user);

        return new HttpOk();
    }

    /**
     * DELETE AN APPLICATION
     *
     * @param jsonInput
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{applicationName}", method = RequestMethod.DELETE)
    public JsonResponse deleteApplication(JsonInput jsonInput)
            throws ServiceException, CheckException {

        jsonInput.validateRemoveApp();

        String applicationName = jsonInput.getApplicationName();
        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        // We must be sure there is no running action before starting new one
        authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        try {
            // Application busy
            applicationService.setStatus(application, Status.PENDING);

            logger.info("delete application :" + applicationName);
            applicationService.remove(application, user);

        } catch (ServiceException e) {
            logger.error(application.toString(), e);
            applicationService.setStatus(application, Status.FAIL);
        }

        logger.info("Application " + applicationName + " is deleted.");

        return new HttpOk();
    }

    /**
     * Return detail information about application
     *
     * @return
     * @throws ServiceException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{applicationName}", method = RequestMethod.GET)
    public Application detail(JsonInput jsonInput)
            throws ServiceException, CheckException {

        jsonInput.validateDetail();

        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, jsonInput.getApplicationName());
        return application;
    }

    /**
     * Return the list of applications for an User
     *
     * @return
     * @throws ServiceException
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public List<Application> findAllByUser()
            throws ServiceException {
        User user = this.authentificationUtils.getAuthentificatedUser();
        List<Application> applications = applicationService.findAllByUser(user);

        logger.debug("Number of applications " + applications.size());
        return applications;
    }

    /**
     * Deploy a web application
     *
     * @return
     * @throws IOException
     * @throws ServiceException
     * @throws CheckException
     */
    @ResponseBody
    @RequestMapping(value = "/{applicationName}/deploy", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public JsonResponse deploy(@RequestPart("file") MultipartFile fileUpload, @PathVariable String applicationName,
                               HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServiceException, CheckException {

        logger.info("applicationName = " + applicationName + "file = " + fileUpload.getOriginalFilename());

        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        // We must be sure there is no running action before starting new one
        authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        applicationManager.deploy(fileUpload, application);

        logger.info("--DEPLOY APPLICATION WAR ENDED--");
        return new HttpOk();
    }

    /**
     * Return the list of containers for an application (module, server or tools)
     *
     * @param applicationName
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @ResponseBody
    @RequestMapping(value = "/{applicationName}/containers", method = RequestMethod.GET)
    public List<ContainerUnit> listContainer(@PathVariable String applicationName)
            throws ServiceException, CheckException {
        logger.debug("applicationName:" + applicationName);
        return applicationService.listContainers(applicationName);
    }

    /* ********************************************************** /
    /*                      ALIAS                               */
    /* ********************************************************** /

     */
    /**
     * Return the list of aliases for an application
     *
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @ResponseBody
    @RequestMapping(value = "/{applicationName}/alias", method = RequestMethod.GET)
    public List<String> aliases(JsonInput jsonInput)
            throws ServiceException, CheckException {
        logger.debug("application.name = " + jsonInput.getApplicationName());
        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, jsonInput.getApplicationName());
        List<String> aliases = applicationService.getListAliases(application);
        if (logger.isDebugEnabled() && aliases != null) logger.debug(aliases.toString());
        return aliases;
    }

    /**
     * Add an alias for an application
     *
     * @param input
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/alias", method = RequestMethod.POST)
    public JsonResponse addAlias(@RequestBody JsonInput input)
            throws ServiceException, CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug(input.toString());
        }

        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, input.getApplicationName());

        CheckUtils.validateInput(input.getApplicationName(), "check.app.name");
        CheckUtils.validateInput(input.getAlias(), "check.alias.name");

        // We must be sure there is no running action before starting new one
        this.authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        this.applicationService.addNewAlias(application, input.getAlias());

        return new HttpOk();
    }

    /**
     * Delete an alias for an application
     *
     * @param jsonInput
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{applicationName}/alias/{alias}", method = RequestMethod.DELETE)
    public JsonResponse removeAlias(JsonInput jsonInput)
            throws ServiceException, CheckException {

        String applicationName = jsonInput.getApplicationName();
        String alias = jsonInput.getAlias();

        if (logger.isDebugEnabled()) {
            logger.debug("application.name=" + applicationName);
            logger.debug("alias.name=" + alias);
        }

        if (applicationName != null) {
            applicationName = applicationName.toLowerCase();
        }
        if (alias != null) {
            alias = alias.toLowerCase();
        }

        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        // We must be sure there is no running action before starting new one
        authentificationUtils.canStartNewAction(user, null, Locale.ENGLISH);

        applicationService.removeAlias(application, alias);

        return new HttpOk();
    }

    /* ********************************************************** /
    /*                      PORTS                               */
    /* ********************************************************** /

    /**
     * Add a port for an application
     *
     * @param input
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/ports", method = RequestMethod.POST)
    public JsonResponse addPort(@RequestBody JsonInput input)
            throws ServiceException, CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug(input.toString());
        }

        String applicationName = input.getApplicationName();
        String nature = input.getPortNature();

        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        CheckUtils.validateOpenPort(input.getPortToOpen(), application);
        CheckUtils.isPortFree(input.getPortToOpen(), application);
        CheckUtils.validateNatureForOpenPortFeature(input.getPortNature(), application);

        Integer port = Integer.parseInt(input.getPortToOpen());
        applicationService.addPort(application, nature, port);

        return new HttpOk();
    }

    /**
     * Delete a port for an application
     *
     * @param input
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{applicationName}/ports/{portToOpen}", method = RequestMethod.DELETE)
    public JsonResponse removePort(JsonInput input)
            throws ServiceException, CheckException {

        String applicationName = input.getApplicationName();

        if (logger.isDebugEnabled()) {
            logger.debug("application.name=" + applicationName);
            logger.debug("application.port=" + input.getPortToOpen());
        }

        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        CheckUtils.validateOpenPort(input.getPortToOpen(), application);
        Integer port = Integer.parseInt(input.getPortToOpen());
        applicationService.removePort(application, port);

        return new HttpOk();
    }

    /**
     * Display env variable for a container
     *
     * @param applicationName
     * @param containerId
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{applicationName}/container/{containerId}/env", method = RequestMethod.GET)
    public JsonResponse displayEnv(@PathVariable String applicationName, @PathVariable String containerId)
            throws ServiceException, CheckException {

        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, applicationName);

        String content = dockerService.exec(containerId, "env");
        System.out.println(content);

        return new HttpOk();
    }
}
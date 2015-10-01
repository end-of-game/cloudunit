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

//
//    LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
//    but CloudUnit is licensed too under a standard commercial license.
//    Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
//    If you are not sure whether the GPL is right for you,
//    you can always test our software under the GPL and inspect the source code before you contact us
//    about purchasing a commercial license.
//
//    LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
//    or promote products derived from this project without prior written permission from Treeptik.
//    Products or services derived from this software may not be called "CloudUnit"
//    nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
//    For any questions, contact us : contact@treeptik.fr
//

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
import fr.treeptik.cloudunit.service.LogService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.CheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
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
 * Controller about Application lifecycle
 * Application is the main concept for CloudUnit : it composed by Server, Module and Metadata
 */
@Controller
@RequestMapping("/application")
public class ApplicationController
    implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Inject
    private ApplicationService applicationService;

    @Inject
    private LogService logService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private MessageSource messageSource;

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
    public JsonResponse isValid(@PathVariable String applicationName,
                                @PathVariable String serverName)
        throws ServiceException,
        CheckException {

        if (this.logger.isInfoEnabled()) {
            this.logger.info("applicationName:" + applicationName);
            this.logger.info("serverName:" + serverName);
        }

        CheckUtils.validateInput(applicationName, this.messageSource
            .getMessage("check.app.name", null, Locale.ENGLISH));
        CheckUtils.validateInput(serverName, this.messageSource.getMessage(
            "check.server.name", null, Locale.ENGLISH));

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

        if (logger.isDebugEnabled()) {
            logger.debug(input.toString());
        }

        CheckUtils.validateSyntaxInput(input.getApplicationName(),
            messageSource.getMessage("check.app.name", null, Locale.ENGLISH));
        CheckUtils.validateInput(input.getServerName(),
            messageSource.getMessage("check.server.name", null, Locale.ENGLISH));

        User user = this.authentificationUtils.getAuthentificatedUser();

        // We must be sure there is no running action before starting new one
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
    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public JsonResponse startApplication(@RequestBody JsonInput input)
        throws ServiceException, CheckException, InterruptedException {

        if (logger.isDebugEnabled()) {
            logger.debug(input.toString());
        }

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
     * @param name
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    public JsonResponse deleteApplication(@PathVariable String name)
        throws ServiceException, CheckException {

        logger.debug("Want to delete the application : " + name);

        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = this.applicationService.findByNameAndUser(
            user, name);

        // We must be sure there is no running action before starting new one
        this.authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        try {
            // Application busy
            applicationService.setStatus(application, Status.PENDING);

            logger.info("delete logs for this application");
            logService.deleteLogsForApplication(name);

            logger.info("delete application :" + name);
            applicationService.remove(application, user);

        } catch (ServiceException e) {
            logger.error(application.toString(), e);
            applicationService.setStatus(application, Status.FAIL);
        }

        logger.debug("Application " + name + " is deleted.");

        return new HttpOk();
    }

    /**
     * Return detail information about application
     *
     * @return
     * @throws ServiceException
     */
    @ResponseBody
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public Application detail(@PathVariable String name)
        throws ServiceException, CheckException {

        logger.debug("name : " + name);

        User user = authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, name);
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
        logger.debug("--CALL LIST USER APPLICATIONS--");

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
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{applicationName}/deploy", method = RequestMethod.POST, consumes = {
        "multipart/form-data"})
    public JsonResponse deploy(@RequestPart("file") MultipartFile fileUpload,
                               @PathVariable String applicationName, HttpServletRequest request,
                               HttpServletResponse response)
        throws IOException, ServiceException,
        CheckException {

        logger.debug("applicationName = " + applicationName);

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
    public List<ContainerUnit> listContainer(
        @PathVariable String applicationName)
        throws ServiceException,
        CheckException {
        logger.debug("applicationName:" + applicationName);
        return applicationService.listContainers(applicationName);
    }

    /**
     * Return the list of aliases for an application
     *
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @ResponseBody
    @RequestMapping(value = "/{name}/alias", method = RequestMethod.GET)
    public List<String> aliases(@PathVariable String name)
        throws ServiceException, CheckException {
        logger.debug("application.name = " + name);
        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(
            user, name);
        return applicationService.getListAliases(application);
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

        // We must be sure there is no running action before starting new one
        this.authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        this.applicationService.addNewAlias(application, input.getAlias());

        return new HttpOk();
    }

    /**
     * Delete an alias for an application
     *
     * @param name
     * @param alias
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @CloudUnitSecurable
    @ResponseBody
    @RequestMapping(value = "/{name}/alias/{alias}", method = RequestMethod.DELETE)
    public JsonResponse removeAlias(@PathVariable String name,
                                    @PathVariable String alias)
        throws ServiceException, CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug("application.name=" + name);
            logger.debug("alias.name=" + alias);
        }

        User user = this.authentificationUtils.getAuthentificatedUser();
        Application application = applicationService.findByNameAndUser(user, name);

        // We must be sure there is no running action before starting new one
        authentificationUtils.canStartNewAction(user, null, Locale.ENGLISH);

        applicationService.removeAlias(application, alias);

        return new HttpOk();
    }

}
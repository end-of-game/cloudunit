package fr.treeptik.cloudunit.manager.impl;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.manager.ApplicationManager;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.FilesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * LifeCycle for an application
 *
 * Created by nicolas on 21/09/15.
 */
@Component
public class ApplicationManagerImpl implements ApplicationManager {

    private Logger logger = LoggerFactory.getLogger(ApplicationManagerImpl.class);

    @Inject
    private ServerService serverService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ModuleService moduleService;

    @Inject
    private MessageSource messageSource;

    /**
     * Create an application
     *
     * @param applicationName
     * @param userLogin
     * @param serverName
     * @throws ServiceException
     * @throws CheckException
     */
    public void create(final String applicationName, final String userLogin, final String serverName)
            throws ServiceException, CheckException {

        Application application = applicationService.create(
                applicationName, userLogin, serverName, null);

        // Wait for the server has a status START (set by shell agent)
        for (Server server : application.getServers()) {
            int counter = 0;
            while (!server.getStatus().equals(Status.START)) {
                if (counter == 60) { break; }
                try { Thread.sleep(1000);} catch (InterruptedException e) { }
                server = serverService.findById(server.getId());
                counter++;
            }
        }

        // Recopy ssh key and update redis
        applicationService.postStart(application, application.getUser());

        int counter = 0;
        while(counter<60) {
            boolean allStarted = true;
            // All modules must be started before application
            for (Module module : application.getModules()) {
                module = moduleService.findById(module.getId());
                while (!module.getStatus().equals(Status.START)) {
                    allStarted = false;
                }
            }
            if (allStarted) break;
            try { Thread.sleep(1000);} catch (InterruptedException e) { }
        }

        // Application is now started
        applicationService.setStatus(application, Status.START);
    }

    /**
     * Start an application
     *
     * @param application
     * @throws ServiceException
     * @throws CheckException
     */
    public void start(Application application, User user)
            throws ServiceException, CheckException {

        try {
            // Application occupée
            applicationService.setStatus(application, Status.PENDING);

            // Application en cours de démarrage
            applicationService.start(application);

            // Wait for the server has a status START (set by shell agent)
            for (Server server : application.getServers()) {
                int counter = 0;
                while (!server.getStatus().equals(Status.START)) {
                    if (counter == 60) { break; }
                    try { Thread.sleep(1000);} catch (InterruptedException e) { }
                    server = serverService.findById(server.getId());
                    counter++;
                }
            }

            // Recopy ssh key and update redis
            applicationService.postStart(application, application.getUser());

            int counter = 0;
            while(counter<60) {
                boolean allStarted = true;
                // All modules must be started before application
                for (Module module : application.getModules()) {
                    module = moduleService.findById(module.getId());
                    while (!module.getStatus().equals(Status.START)) {
                        allStarted = false;
                    }
                }
                if (allStarted) break;
                try { Thread.sleep(1000);} catch (InterruptedException e) { }
            }

            applicationService.setStatus(application, Status.START);

        } catch (ServiceException e) {
            applicationService.setStatus(application, Status.FAIL);
        }
    }

    /**
     * Stop an application
     *
     * @param application
     * @throws ServiceException
     * @throws CheckException
     */
    public void stop(Application application, User user)
            throws ServiceException, CheckException {
        try {
            // Application occupée
            applicationService.setStatus(application, Status.PENDING);

            // Arrêt de l'application
            applicationService.stop(application);

            // application stop
            applicationService.setStatus(application, Status.STOP);

        } catch (ServiceException e) {
            // Anomaly
            applicationService.setStatus(application, Status.FAIL);
        }
    }

    /**
     * Deployment for an archive
     *
     * @param fileUpload
     * @param application
     */
    public void deploy(MultipartFile fileUpload, Application application)
    throws ServiceException, CheckException {
        try {
            // Deployment processus with verification for format file
            if (FilesUtils.isAuthorizedFileForDeployment(fileUpload.getOriginalFilename())) {

                File file = File.createTempFile("deployment-", FilesUtils.setSuffix(fileUpload.getOriginalFilename()));
                fileUpload.transferTo(file);

                if (application.getStatus().equals(Status.STOP)) {
                    throw new CheckException(messageSource.getMessage("app.stop", null, Locale.ENGLISH));
                }

                // Application busy
                applicationService.setStatus(application, Status.PENDING);

                // Deployment
                applicationService.deploy(file, application);

                // application is started
                applicationService.setStatus(application, Status.START);

            } else {
                throw new CheckException(messageSource.getMessage("check.war.ear", null, Locale.ENGLISH));
            }
        } catch(IOException e) {
            applicationService.setStatus(application, Status.FAIL);
            throw new ServiceException(e.getMessage());
        }
    }
}

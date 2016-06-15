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

package fr.treeptik.cloudunit.manager.impl;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.manager.ApplicationManager;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.FilesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

/**
 * LifeCycle for an application
 * Created by nicolas on 21/09/15.
 */
@Component
public class ApplicationManagerImpl
        implements ApplicationManager {

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
                applicationName, userLogin, serverName, null, null);

        // Wait for the server has a status START (set by shell agent)
        for (Server server : application.getServers()) {
            int counter = 0;
            while (!server.getStatus().equals(Status.START)) {
                if (counter == 60) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }
                server = serverService.findById(server.getId());
                counter++;
            }
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

            // Wait for the server has a status START
            for (Server server : application.getServers()) {
                System.out.println(server.getStatus());
                int counter = 0;
                while (server.getStatus() != null && !server.getStatus().equals(Status.START)) {
                    System.out.println(server.getStatus());
                    if (counter == 60) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    server = serverService.findById(server.getId());
                    counter++;
                }
            }

            // Wait for the module has a status START (set by shell agent)
            for (Module module : application.getModules()) {
                int counter = 0;
                while (module.getStatus() != null && !module.getStatus().equals(Status.START)) {
                    System.out.println(module.getStatus());
                    if (counter == 60) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    module = moduleService.findById(module.getId());
                    counter++;
                }
            }

            applicationService.setStatus(application, Status.START);

        } catch (ServiceException e) {
            logger.error(application.toString(), e);
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
            logger.debug(application.toString());

            // Deployment processus with verification for format file
            if (FilesUtils.isAuthorizedFileForDeployment(fileUpload.getOriginalFilename())) {

                File file = File.createTempFile("deployment-",
                        FilesUtils.setSuffix(fileUpload.getOriginalFilename()));
                file.renameTo(new File(fileUpload.getOriginalFilename()));
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
        } catch (IOException e) {
            e.printStackTrace();
            applicationService.setStatus(application, Status.FAIL);
            throw new ServiceException(e.getMessage());
        }
    }
}

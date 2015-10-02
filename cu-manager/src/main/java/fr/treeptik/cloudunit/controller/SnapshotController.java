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

package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonInput;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Snapshot;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.SnapshotService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.CheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/snapshot")
public class SnapshotController {

    private final Logger logger = LoggerFactory.getLogger(SnapshotController.class);

    // Default Locale
    private final Locale locale = Locale.ENGLISH;

    @Inject
    private SnapshotService snapshotService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private MessageSource messageSource;

    @RequestMapping(method = RequestMethod.POST)
    public JsonResponse create(@RequestBody JsonInput input)
        throws ServiceException, CheckException {
        System.out.println("ok");

        CheckUtils.validateSyntaxInput(input.getTag(), this.messageSource
            .getMessage("check.snapshot.name", null, Locale.ENGLISH));

        Application application;
        User user = null;
        try {

            user = authentificationUtils.getAuthentificatedUser();
            application = applicationService.findByNameAndUser(user, input.getApplicationName());

            // To be protected from WebUI uncontrolled requests (angularjs timeout)
            if (application.getUser().getStatus()
                .equals(User.STATUS_NOT_ALLOWED)) {
                logger.info("Dispatch request");
                return new HttpOk();
            }

            authentificationUtils.forbidUser(user);

            // We must be sure there is no running action before starting new one
            this.authentificationUtils.canStartNewAction(null, application, locale);

            Status previousStatus = application.getStatus();

            applicationService.setStatus(application, Status.PENDING);
            snapshotService.create(
                input.getApplicationName(),
                user,
                input.getTag(),
                input.getDescription(),
                previousStatus);
            applicationService.setStatus(application, previousStatus);

        } finally {
            authentificationUtils.allowUser(user);
        }
        return new HttpOk();

    }

    /**
     * List all snapshots
     *
     * @return
     * @throws ServiceException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public List<Snapshot> listAll()
        throws ServiceException {
        User user = authentificationUtils.getAuthentificatedUser();
        return snapshotService.listAll(user.getLogin());
    }

    /**
     * Delete a snapshot
     *
     * @param tag
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{tag}")
    public JsonResponse remove(@PathVariable String tag)
        throws ServiceException, CheckException {
        User user = authentificationUtils.getAuthentificatedUser();
        snapshotService.remove(tag, user.getLogin());
        return new HttpOk();
    }

    /**
     * Clone an application from a snapshot
     * It could be a restore or a new one.
     *
     * @param input
     * @return
     * @throws ServiceException
     * @throws InterruptedException
     */
    @RequestMapping(method = RequestMethod.POST, value = "/clone")
    public JsonResponse clone(@RequestBody JsonInput input)
        throws ServiceException, InterruptedException, CheckException {

        if (logger.isInfoEnabled()) {
            logger.info(input.toString());
            logger.info(input.getClientSource());
        }

        User user = authentificationUtils.getAuthentificatedUser();
        if (user.getStatus().equals(User.STATUS_NOT_ALLOWED)) {
            logger.warn("Request dispatched");
            return null;
        }

        // Forbid the user for any other action
        authentificationUtils.forbidUser(user);

        try {
            snapshotService.cloneFromASnapshot(input.getApplicationName(), input.getTag());

            Application application = applicationService.findByNameAndUser(user, input.getApplicationName());
            applicationService.stop(application);
            applicationService.setStatus(application, Status.STOP);

        } finally {
            // in all cases, we must allow the user to work again
            authentificationUtils.allowUser(user);
        }

        return new HttpOk();
    }
}

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


package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Locale;

@Component
public class AuthentificationUtils {

    private Logger logger = LoggerFactory
        .getLogger(AuthentificationUtils.class);

    @Inject
    private UserService userService;

    @Inject
    private MessageSource messageSource;

    public User getAuthentificatedUser()
        throws ServiceException {
        Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();
        return userService.findByLogin(auth.getName());
    }

    public void allowUser(User user)
        throws ServiceException {
        user.setStatus(User.STATUS_ACTIF);
        userService.update(user);
    }

    public void forbidUser(User user)
        throws ServiceException {
        user.setStatus(User.STATUS_NOT_ALLOWED);
        userService.update(user);
    }

    /**
     * Method to verify if any actions is authorized to be launched. Maybe a
     * backup or restore occurs so the user must wait for the end of processus
     *
     * @throws ServiceException
     * @throws CheckException
     */
    public void canStartNewAction(User user, Application application,
                                  Locale locale)
        throws CheckException {

        if (user != null && user.getStatus().equals(User.STATUS_NOT_ALLOWED)) {
            throw new CheckException(
                "You have launched a backup or a restore operation and it is still performing. Please wait a moment to continue");
        }

        // check if there is no action currently on the entity
        if (application != null
            && application.getStatus().equals(Status.PENDING)) {
            throw new CheckException(this.messageSource.getMessage(
                "app.pending", null, locale));
        }
    }

}

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

package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.inject.Inject;
import java.util.Locale;

/**
 * Created by nicolas on 27/09/15.
 */
public class CloudUnitAbstractAspect {

    protected Locale locale = Locale.ENGLISH;

    @Inject
    protected UserService userService;

    protected User getAuthentificatedUser()
        throws ServiceException {
        UserDetails principal = (UserDetails) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        User user = userService.findByLogin(principal.getUsername());
        return user;
    }
}

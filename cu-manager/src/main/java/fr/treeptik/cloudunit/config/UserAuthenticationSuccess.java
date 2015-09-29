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

package fr.treeptik.cloudunit.config;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;

@Component
public class UserAuthenticationSuccess
                implements
                ApplicationListener<AuthenticationSuccessEvent>
{

    private Logger logger = LoggerFactory
                    .getLogger( UserAuthenticationSuccess.class );

    @Inject
    private UserService userService;

    @Override
    public void onApplicationEvent( AuthenticationSuccessEvent event )
    {
        try
        {
            User user = userService.findByLogin( ( (UserDetails) event
                            .getAuthentication().getPrincipal() ).getUsername() );
            user.setLastConnection( new Date() );
            userService.update( user );
        }
        catch ( ServiceException e )
        {
            e.printStackTrace();
        }
    }

}

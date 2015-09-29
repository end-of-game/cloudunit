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

package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.inject.Inject;

/**
 * Created by nicolas on 31/08/15.
 */
@Aspect
public class ServerAnnotationAspect
{

    @Inject
    private UserService userService;

    @After( "@annotation(fr.treeptik.cloudunit.aspects.Loggable)" )
    public void myAdvice( JoinPoint point )
    {

        try
        {
            UserDetails principal = (UserDetails) SecurityContextHolder
                            .getContext().getAuthentication().getPrincipal();
            User user = this.userService.findByLogin( principal.getUsername() );
            System.out.println( "Executing myAdvice!! " + user );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

    }

}

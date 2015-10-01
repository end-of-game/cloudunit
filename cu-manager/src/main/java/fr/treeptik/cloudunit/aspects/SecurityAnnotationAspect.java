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

import fr.treeptik.cloudunit.dto.JsonInput;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Annotation needed to verify that an user has a real access to a application for its lifecyle.
 * So we could stop an intrusion if user1 wants to stop an application of user2 for example.
 *
 */
@Component
@Aspect
public class SecurityAnnotationAspect {

    private final Logger logger = LoggerFactory.getLogger(SecurityAnnotationAspect.class);

    @Inject
    private UserService userService;

    @Inject
    private ApplicationService applicationService;

    @Before("@annotation(fr.treeptik.cloudunit.aspects.CloudUnitSecurable)")
    public void verifyRelationBetweenUserAndApplication(JoinPoint joinPoint) {

        UserDetails principal = null;
        JsonInput jsonInput = null;
        try {
            principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.findByLogin(principal.getUsername());

            if (joinPoint.getArgs() == null) {
                logger.error("Error on annotation aspect : " + joinPoint.getStaticPart().getSignature());
            } else {
                String applicationName = null;
                if (joinPoint.getArgs()[0] instanceof JsonInput) {
                    jsonInput = (JsonInput) joinPoint.getArgs()[0];
                    applicationName = jsonInput.getApplicationName();
                } else {
                    // The first parameter must be always be the applicationName
                    applicationName = (String) joinPoint.getArgs()[0];
                }
                Application application = applicationService.findByNameAndUser(user, applicationName);
                if (application == null) {
                    throw new IllegalArgumentException();
                }
            }

        } catch (ServiceException | CheckException e) {
            logger.error(principal.toString() + ", " + jsonInput, e);
        }

    }

}

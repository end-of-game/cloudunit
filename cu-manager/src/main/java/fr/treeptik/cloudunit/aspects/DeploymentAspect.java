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

import fr.treeptik.cloudunit.exception.MonitorException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.utils.MessageUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;

@Component
@Aspect
public class DeploymentAspect
    extends CloudUnitAbstractAspect
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String createType = "CREATE";

    private final Logger logger = LoggerFactory.getLogger(DeploymentAspect.class);

    @Inject
    private MessageService messageService;

    // Before methods
    @Before("execution(* fr.treeptik.cloudunit.service.DeploymentService.create(..))")
    public void beforeDeployment(JoinPoint joinPoint)
        throws MonitorException {
        try {
            User user = this.getAuthentificatedUser();
            Application application = (Application) joinPoint.getArgs()[0];
            Message message = null;
            switch (joinPoint.getSignature().getName().toUpperCase()) {
                case createType:
                    message = MessageUtils.writeBeforeDeploymentMessage(user,
                        application, createType);
                    break;
            }
            if (message != null) {
                logger.info(message.toString());
                messageService.create(message);
            }

        } catch (ServiceException e) {
            throw new MonitorException("Error afterReturningApplication", e);

        }
    }

    @AfterReturning(pointcut = "execution(* fr.treeptik.cloudunit.service.DeploymentService.create(..))", returning = "result")
    public void afterReturningDeployment(StaticPart staticPart, Object result)
        throws MonitorException {
        try {
            if (result == null)
                return;
            Deployment deployment = (Deployment) result;
            User user = deployment.getApplication().getUser();
            Message message = null;
            switch (staticPart.getSignature().getName().toUpperCase()) {
                case createType:
                    message = MessageUtils.writeDeploymentMessage(user, deployment,
                        createType);
                    break;
            }

            if (message != null) {
                logger.info(message.toString());
                messageService.create(message);
            }

        } catch (ServiceException e) {
            throw new MonitorException("Error afterReturningApplication", e);

        }
    }

}

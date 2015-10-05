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

import fr.treeptik.cloudunit.exception.MonitorException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.utils.MessageUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;

@Component
@Aspect
public class ApplicationAspect
    extends CloudUnitAbstractAspect
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String createType = "CREATE";

    private final String deleteType = "REMOVE";

    private final String startType = "START";

    private final String stopType = "STOP";

    private final Logger logger = LoggerFactory.getLogger(ApplicationAspect.class);

    @Inject
    private MessageService messageService;

    @Inject
    private MessageSource messageSource;

    /**
     * @param joinPoint
     * @throws MonitorException
     * @throws ServiceException
     */
    @Before("execution(* fr.treeptik.cloudunit.service.ApplicationService.remove(..)) " +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.create(..)) " +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.start(..))" +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.stop(..))")
    public void beforeApplication(JoinPoint joinPoint)
        throws MonitorException, ServiceException {

        String applicationName = null;
        if (joinPoint.getArgs()[0] instanceof String) {
            applicationName = (String) joinPoint.getArgs()[0];
        } else if (joinPoint.getArgs()[0] instanceof Application) {
            applicationName = ((Application) joinPoint.getArgs()[0]).getName();
        }

        Message message = null;
        User user = getAuthentificatedUser();

        switch (joinPoint.getSignature().getName().toUpperCase()) {
            case createType:
                message = MessageUtils.writeBeforeApplicationMessage(user,
                    applicationName, createType);
                break;
            case deleteType:
                message = MessageUtils.writeBeforeApplicationMessage(user,
                    applicationName, deleteType);
                break;
            case startType:
                message = MessageUtils.writeBeforeApplicationMessage(user,
                    applicationName, startType);
                break;
            case stopType:
                message = MessageUtils.writeBeforeApplicationMessage(user,
                    applicationName, stopType);
                break;
        }
        if (message != null) {
            logger.info(message.toString());
            messageService.create(message);
        }

    }

    @AfterThrowing(pointcut = "execution(* fr.treeptik.cloudunit.service.ApplicationService.remove(..)) " +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.create(..)) " +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.start(..))" +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.stop(..))"
        , throwing = "e")
    public void afterThrowingApplication(StaticPart staticPart,
                                         Exception e)
        throws ServiceException {

        User user = this.getAuthentificatedUser();
        Message message = null;
        logger.debug("CALLED CLASS : " + staticPart.getSignature().getName());
        switch (staticPart.getSignature().getName().toUpperCase()) {
            case createType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                    user, createType, messageSource,
                    locale);
                break;
            case deleteType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                    user, deleteType, messageSource,
                    locale);
                break;
            case startType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                    user, startType, messageSource,
                    locale);
                break;
            case stopType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                    user, stopType, messageSource,
                    locale);
                break;
        }
        if (message != null) {
            messageService.create(message);
        }
    }

    @AfterReturning(pointcut = "execution(* fr.treeptik.cloudunit.service.ApplicationService.remove(..)) " +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.create(..)) " +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.start(..))" +
        "|| execution(* fr.treeptik.cloudunit.service.ApplicationService.stop(..))", returning = "result")
    public void afterReturningApplication(StaticPart staticPart, Object result)
        throws MonitorException {

        try {
            if (result == null)
                return;

            Application application = (Application) result;
            User user = application.getUser();
            Message message = null;

            switch (staticPart.getSignature().getName().toUpperCase()) {

                case createType:
                    message = MessageUtils.writeAfterReturningApplicationMessage(
                        user, application, createType);
                    break;
                case deleteType:
                    message = MessageUtils.writeAfterReturningApplicationMessage(
                        user, application, deleteType);
                    break;
                case startType:
                    message = MessageUtils.writeAfterReturningApplicationMessage(
                        user, application, startType);
                    break;
                case stopType:
                    message = MessageUtils.writeAfterReturningApplicationMessage(
                        user, application, stopType);
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

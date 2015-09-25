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
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.Snapshot;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.MessageUtils;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;

@Component
@Aspect
public class SnapshotAspect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String createType = "CREATE";
    private final String deleteType = "REMOVE";
    private final String cloneFromASnapshot = "CLONEFROMASNAPSHOT";

    Locale locale = Locale.ENGLISH;

    private Logger logger = LoggerFactory.getLogger(SnapshotAspect.class);

    @Inject
    private MessageService messageService;

    @Inject
    private UserService userService;

    @Inject
    private MessageSource messageSource;

    // Before methods
    @AfterReturning(pointcut = "execution(!java.util.List fr.treeptik.cloudunit.service.SnapshotService.create(..))" +
            " || execution(!java.util.List fr.treeptik.cloudunit.service.SnapshotService.remove(..))" +
            " || execution(!java.util.List fr.treeptik.cloudunit.service.SnapshotService.cloneFromASnapshot(..))",
            returning = "result")
    public void afterReturningSnapshot(StaticPart staticPart, Object result)
            throws MonitorException {
        try {
            Snapshot snapshot = (Snapshot) result;
            User user = this.getAuthentificatedUser();
            Message message = null;
            switch (staticPart.getSignature().getName().toUpperCase()) {
                case createType:
                    message = MessageUtils.writeSnapshotMessage(user, snapshot,
                            createType);
                    break;
                case deleteType:
                    message = MessageUtils.writeSnapshotMessage(user, snapshot,
                            deleteType);
                    break;

                case cloneFromASnapshot:
                    message = MessageUtils.writeSnapshotMessage(user, snapshot,
                            cloneFromASnapshot);
                    break;

            }
            logger.info(message.toString());
            messageService.create(message);

        } catch (ServiceException e) {
            throw new MonitorException("Error afterReturningSnapshot", e);

        }
    }

    @AfterThrowing(pointcut = "execution(!java.util.List fr.treeptik.cloudunit.service.SnapshotService.create(..))" +
            " || execution(!java.util.List fr.treeptik.cloudunit.service.SnapshotService.remove(..))" +
            " || execution(!java.util.List fr.treeptik.cloudunit.service.SnapshotService.cloneFromASnapshot(..))",
            throwing = "e")
    public void afterThrowingSnapshot(final StaticPart staticPart,
                                      final Exception e) throws ServiceException {
        User user = this.getAuthentificatedUser();
        Message message = null;
        logger.debug("CALLED CLASS : " + staticPart.getSignature().getName());
        switch (staticPart.getSignature().getName().toUpperCase()) {
            case createType:
                message = MessageUtils.writeAfterThrowingSnapshotMessage(e, user,
                        createType);
            case deleteType:
                message = MessageUtils.writeAfterThrowingSnapshotMessage(e, user,
                        deleteType);
            case cloneFromASnapshot:
                message = MessageUtils.writeAfterThrowingSnapshotMessage(e, user,
                        cloneFromASnapshot);
                break;
        }
        messageService.create(message);
    }


    private User getAuthentificatedUser() throws ServiceException {
        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user = userService.findByLogin(principal.getUsername());
        return user;
    }

}

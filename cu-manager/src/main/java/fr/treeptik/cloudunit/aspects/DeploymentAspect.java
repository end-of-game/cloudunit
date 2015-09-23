package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.exception.MonitorException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.MessageUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
public class DeploymentAspect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String createType = "CREATE";
    private final String updateType = "UPDATE";
    private final String deleteType = "REMOVE";
    private final String startType = "START";
    private final String stopType = "STOP";
    private final String restartType = "RESTART";

    // default locale
    Locale locale = Locale.ENGLISH;
    private Logger logger = LoggerFactory.getLogger(DeploymentAspect.class);
    @Inject
    private MessageService messageService;
    @Inject
    private UserService userService;
    @Inject
    private MessageSource messageSource;

    // Before methods
    @Before("execution(* fr.treeptik.cloudunit.service.DeploymentService.create(..))")
    public void beforeDeployment(JoinPoint joinPoint) throws MonitorException {
        try {
            User user = null;
            if (!joinPoint.getArgs()[1].equals(Type.GITPUSH)) {
                user = this.getAuthentificatedUser();
            }
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
            if (result == null) return;
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


    private User getAuthentificatedUser() throws ServiceException {
        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user = userService.findByLogin(principal.getUsername());
        return user;
    }

}

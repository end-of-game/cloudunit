package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.exception.MonitorException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.MessageUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@Aspect
public class ApplicationAspect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String createType = "CREATE";
    private final String deleteType = "REMOVE";
    private final String startType = "START";
    private final String stopType = "STOP";
    private final String restartType = "RESTART";
    Locale locale = Locale.ENGLISH;
    private Logger logger = LoggerFactory.getLogger(ApplicationAspect.class);
    @Inject
    private MessageService messageService;
    @Inject
    private UserService userService;
    @Inject
    private MessageSource messageSource;

    /**
     *
     * @param joinPoint
     * @throws MonitorException
     * @throws ServiceException
     */
    @Before("execution(!java.util.List fr.treeptik.cloudunit.service.ApplicationService.*(..))")
    public void beforeApplication(final JoinPoint joinPoint)
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
            case restartType:
                message = MessageUtils.writeBeforeApplicationMessage(user,
                        applicationName, restartType);
                break;
        }
        if (message != null) {
            logger.info(message.toString());
            messageService.create(message);
        }

    }


    @AfterThrowing(pointcut = "execution(!java.util.List fr.treeptik.cloudunit.service.ApplicationService.*(..))", throwing = "e")
    public void afterThrowingApplication(final StaticPart staticPart,
                                         final Exception e) throws ServiceException {
        User user = this.getAuthentificatedUser();
        Message message = null;
        logger.debug("CALLED CLASS : " + staticPart.getSignature().getName());
        switch (staticPart.getSignature().getName().toUpperCase()) {
            case createType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                        user, createType, messageSource, locale);
                break;
            case deleteType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                        user, deleteType, messageSource, locale);
                break;
            case startType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                        user, startType, messageSource, locale);
                break;
            case stopType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                        user, stopType, messageSource, locale);
                break;
            case restartType:
                message = MessageUtils.writeAfterThrowingApplicationMessage(e,
                        user, restartType, messageSource, locale);
                break;
        }
        if (message != null) {
            messageService.create(message);
        }
    }


    @AfterReturning(pointcut = "execution(* fr.treeptik.cloudunit.service.ApplicationService.*(..))", returning = "result")
    public void afterReturningApplication(StaticPart staticPart, Object result)
            throws MonitorException {
        try {
            if (result == null) return;
            List<String> methods = Arrays.asList(createType, deleteType, startType, stopType, restartType);
            if (!methods.contains(staticPart.getSignature().getName().toUpperCase())) return;

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
                case restartType:
                    message = MessageUtils.writeAfterReturningApplicationMessage(
                            user, application, restartType);
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

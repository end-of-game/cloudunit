package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.exception.MonitorException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.MessageUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;

public class ServerAspect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String createType = "CREATE";
    private final String updateType = "UPDATE";

    Locale locale = Locale.ENGLISH;

    private Logger logger = LoggerFactory.getLogger(ServerAspect.class);

    @Inject
    private MessageService messageService;

    @Inject
    private UserService userService;

    @Inject
    private MessageSource messageSource;

    // Before methods
    @Before("execution(* fr.treeptik.cloudunit.service.ServerService.create(..))")
    public void beforeServer(final JoinPoint joinPoint)
            throws MonitorException, ServiceException {

        Server server = (Server) joinPoint.getArgs()[0];
        User user = getAuthentificatedUser();
        Message message = null;
        String applicationName = server.getApplication().getName();

        switch (joinPoint.getSignature().getName().toUpperCase()) {
            case updateType:
                message = MessageUtils.writeBeforeApplicationMessage(user,
                        applicationName, updateType);
                break;
        }
        logger.info(message.toString());
        messageService.create(message);

    }

    @AfterReturning(pointcut = "execution(* fr.treeptik.cloudunit.service.ServerService.*(..))", returning = "result")
    public void afterReturningServer(StaticPart staticPart, Object result)
            throws MonitorException {
        try {
            if (result == null) return;
            Server server = (Server) result;
            User user = server.getApplication().getUser();
            Message message = null;
            switch (staticPart.getSignature().getName().toUpperCase()) {
                case createType:
                    message = MessageUtils.writeServerMessage(user, server,
                            createType);
                    break;
                case updateType:
                    message = MessageUtils.writeServerMessage(user, server,
                            updateType);
                    break;

            }
            logger.info(message.toString());
            messageService.create(message);

        } catch (ServiceException e) {
            throw new MonitorException("Error afterReturningApplication", e);
        }
    }

    @AfterThrowing(pointcut = "execution(* fr.treeptik.cloudunit.service.ServerService.*(..))", throwing = "e")
    public void afterThrowingServer(final StaticPart staticPart,
                                    final Exception e) throws ServiceException {
        User user = this.getAuthentificatedUser();
        Message message = null;
        logger.debug("CALLED CLASS : " + staticPart.getSignature().getName());
        switch (staticPart.getSignature().getName().toUpperCase()) {
            case updateType:
                message = MessageUtils.writeAfterThrowingModuleMessage(e, user,
                        updateType);
                break;
        }
        if (message != null) {
            messageService.create(message);
        }
    }


    private User getAuthentificatedUser() throws ServiceException {
        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user = userService.findByLogin(principal.getUsername());
        return user;
    }

}

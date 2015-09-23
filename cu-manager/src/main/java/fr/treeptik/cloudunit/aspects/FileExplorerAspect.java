package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

@Aspect
@Component
public class FileExplorerAspect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String createType = "CREATE";
    private final String updateType = "UPDATE";
    private final String deleteType = "REMOVE";
    private final String startType = "START";
    private final String stopType = "STOP";
    private final String restartType = "RESTART";

    Locale locale = Locale.ENGLISH;

    private Logger logger = LoggerFactory.getLogger(FileExplorerAspect.class);

    @Inject
    private MessageService messageService;

    @Inject
    private UserService userService;

    @Inject
    private MessageSource messageSource;

    @AfterReturning("execution(!java.util.List fr.treeptik.cloudunit.service.FileService.*(..))")
    public void afterReturningFileExplorer(final JoinPoint joinPoint)
            throws ServiceException {
        Message message = new Message();
        User user = this.getAuthentificatedUser();
        message.setDate(new Date());
        message.setType(Message.INFO);
        message.setAuthor(user);
        message.setApplicationName((String) joinPoint.getArgs()[0]);

        switch (joinPoint.getSignature().getName().toUpperCase()) {
            case "DELETEFILESFROMCONTAINER":

                message.setEvent(user.getLogin() + " has removed this file : "
                        + joinPoint.getArgs()[2]);
                break;
            case "SENDFILETOCONTAINER":
                message.setEvent(user.getLogin() + " has send this file : "
                        + joinPoint.getArgs()[3] + " at "
                        + joinPoint.getArgs()[4].toString().replaceAll("__", "/"));
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

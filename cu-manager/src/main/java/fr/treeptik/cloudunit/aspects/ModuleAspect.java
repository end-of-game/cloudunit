package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.exception.MonitorException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.UserService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;

@Aspect
@Component
public class ModuleAspect implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String initModule = "INITMODULE";
    private final String createType = "CREATE";
    private final String deleteType = "REMOVE";
    private final String addModule = "ADDMODULE";

    Locale locale = Locale.ENGLISH;

    private Logger logger = LoggerFactory.getLogger(ModuleAspect.class);

    @Inject
    private MessageService messageService;

    @Inject
    private UserService userService;

    @Inject
    private MessageSource messageSource;

    @Before("execution(!java.util.List fr.treeptik.cloudunit.service.ModuleService.*(..))")
    public void beforeModule(final JoinPoint joinPoint)
            throws MonitorException, ServiceException {

        User user = getAuthentificatedUser();
        Message message = null;

        Module module = null;
        switch (joinPoint.getSignature().getName().toUpperCase()) {

            case initModule:
                Application application = (Application) joinPoint.getArgs()[0];
                module = (Module) joinPoint.getArgs()[1];
                if (!module.getName().contains("git")) {
                    message = MessageUtils.writeBeforeModuleMessage(user,
                            module.getName(), application.getName(), createType);
                    logger.info(message.toString());
                    messageService.create(message);
                }
                break;

            case deleteType:
                module = (Module) joinPoint.getArgs()[2];
                if (!module.getName().contains("git")) {
                    message = MessageUtils
                            .writeBeforeModuleMessage(user, module.getName(),
                                    ((User) joinPoint.getArgs()[1]).getLogin(), deleteType);
                    logger.info(message.toString());
                    messageService.create(message);
                }
                break;

        }

    }

    @AfterReturning(pointcut = "execution(!java.util.List fr.treeptik.cloudunit.service.ModuleService.*(..))", returning = "result")
    public void afterReturningModule(StaticPart staticPart, Object result)
            throws MonitorException {
        try {
            if (result == null) return;
            Module module = (Module) result;
            User user = module.getApplication().getUser();
            // scape tool module
            if (!module.getImage().getImageType().equalsIgnoreCase("tool")) {
                Message message = null;
                switch (staticPart.getSignature().getName().toUpperCase()) {
                    case initModule:
                        message = MessageUtils.writeModuleMessage(user, module,
                                createType);
                        break;

                    case deleteType:
                        message = MessageUtils.writeModuleMessage(user, module,
                                deleteType);
                        break;
                }
                if (message != null) {
                    logger.info(message.toString());
                    messageService.create(message);
                }
            }
        } catch (ServiceException e) {
            throw new MonitorException("Error afterReturningApplication", e);

        }
    }

    @AfterThrowing(pointcut = "execution(!java.util.List fr.treeptik.cloudunit.service.ModuleService.*(..))", throwing = "e")
    public void afterThrowingModule(final StaticPart staticPart,
                                    final Exception e) throws ServiceException {
        User user = this.getAuthentificatedUser();
        Message message = null;
        logger.debug("CALLED CLASS : " + staticPart.getSignature().getName());
        switch (staticPart.getSignature().getName().toUpperCase()) {
            case addModule:
                message = MessageUtils.writeAfterThrowingModuleMessage(e, user,
                        createType);
                break;
            case deleteType:
                message = MessageUtils.writeAfterThrowingModuleMessage(e, user,
                        deleteType);
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

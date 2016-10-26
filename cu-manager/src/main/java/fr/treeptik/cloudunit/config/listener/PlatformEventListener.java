package fr.treeptik.cloudunit.config.listener;

import fr.treeptik.cloudunit.config.events.DatabaseConnectionFailEvent;
import fr.treeptik.cloudunit.config.events.UnexpectedContainerStatusEvent;
import fr.treeptik.cloudunit.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

/**
 * Created by guillaume on 08/10/16.
 */
@Component
public class PlatformEventListener {

    private Logger logger = LoggerFactory.getLogger(PlatformEventListener.class);

    @Autowired
    private ApplicationContext appContext;

    @Value("${admin.email}")
    private String adminEmail;


    @EventListener
    @Async
    public void onDatasourceConnectionFail(DatabaseConnectionFailEvent databaseConnectionFailEvent){
        String subject = "Datasource connection fails";
        String message = (String) databaseConnectionFailEvent.getSource();
        logger.error("A platform error has occured : " + message);
        sendEmailToAdmin(subject, message);
    }

    @EventListener
    @Async
    public void onUnexpectedContainerState(UnexpectedContainerStatusEvent unexpectedContainerStatusEvent){
        String subject = "A container is in inconsistent state";
        String message = (String) unexpectedContainerStatusEvent.getSource();
        logger.error("A platform error has occured : " + message);
        sendEmailToAdmin(subject, message);
    }

    private void sendEmailToAdmin(String subject, String message) {
            try {
                EmailService emailService = appContext.getBean(EmailService.class);
                emailService.sendTextMail(message, subject, adminEmail);
            } catch (NoSuchBeanDefinitionException e) {
                logger.warn("Email sending is not activated");
            } catch (MessagingException e) {
                logger.error("Email sending fail : " + message);
                e.printStackTrace();
            }
        }

}

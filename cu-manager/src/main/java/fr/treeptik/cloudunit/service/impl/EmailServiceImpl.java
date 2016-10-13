package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.config.EmailActiveCondition;
import fr.treeptik.cloudunit.service.EmailService;
import org.springframework.context.annotation.Conditional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by guillaume on 09/10/16.
 */
@Service
@Conditional(value = EmailActiveCondition.class)
public class EmailServiceImpl implements EmailService{

    @Inject
    private JavaMailSender mailSender;

    @Override
    public void sendTextMail(final String textContent, final String subject,
                             final String recipientEmail)
            throws MessagingException {

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(subject);
        message.setFrom("admin@treeptik.fr");
        message.setTo(recipientEmail);
        message.setText(textContent);

       mailSender.send(mimeMessage);
    }
}

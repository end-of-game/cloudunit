package fr.treeptik.cloudunit.service;

import javax.mail.MessagingException;

/**
 * Created by guillaume on 09/10/16.
 */
public interface EmailService {


    void sendTextMail(String textContent, String subject,
                      String recipientEmail)
            throws MessagingException;
}

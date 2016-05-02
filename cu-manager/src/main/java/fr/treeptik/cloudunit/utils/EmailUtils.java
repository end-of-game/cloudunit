/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@Component
public class EmailUtils {

    @Inject
    ApplicationContext applicationContext;

    @Inject
    MessageSource messageSource;

    private Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    @Value("${mail.emailFrom}")
    private String emailFrom;

    @Value("${mail.apiKey}")
    private String apiKey;

    @Value("${mail.secretKey}")
    private String secretKey;

    @Value("${mail.smtpHost}")
    private String smtpHost;

    @Value("${mail.socketFactoryPort}")
    private String socketFactoryPort;

    @Value("${mail.smtpPort}")
    private String smtpPort;

    @Value("${email.force.redirect}")
    private String emailForceRedirect;

    /**
     * Store general propreties (mailJet), create session and addressFrom
     *
     * @param mapConfigEmail
     * @return
     * @throws AddressException
     * @throws MessagingException
     */
    private Map<String, Object> initEmailConfig(
        Map<String, Object> mapConfigEmail)
        throws AddressException,
        MessagingException {

        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(this.getClass(),
            "/fr.treeptik.cloudunit.templates/");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.socketFactory.port", socketFactoryPort);
        props.put("mail.smtp.socketFactory.class",
            "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getDefaultInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(apiKey, secretKey);
                }
            });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom));

        mapConfigEmail.put("message", message);
        mapConfigEmail.put("configuration", configuration);

        return mapConfigEmail;

    }

    /**
     * parse emailType of mapConfigEmail and redirect on appropriate construct
     * method
     *
     * @param mapConfigEmail
     * @return
     * @throws MessagingException
     */
    private Map<String, Object> defineEmailType(
        Map<String, Object> mapConfigEmail)
        throws MessagingException {
        String emailType = (String) mapConfigEmail.get("emailType");

        if (emailType.equals("activation")) {

            mapConfigEmail = this.constructActivationEmail(mapConfigEmail);
        }
        if (emailType.equals("moduleInformations")) {

            mapConfigEmail = this
                .constructModuleInformationsEmail(mapConfigEmail);

        }
        /*
        if (emailType.equals("changeEmail")) {

			mapConfigEmail = this.constructChangeEmail(mapConfigEmail);
		}

		if (emailType.equals("sendPassword")) {

			mapConfigEmail = this.constructSendPasswordEmail(mapConfigEmail);
		}
        */
        return mapConfigEmail;
    }

    /**
     * public method to send mail parameter is map with emailType, user
     *
     * @param mapConfigEmail
     * @throws MessagingException
     */
    @Async
    public void sendEmail(Map<String, Object> mapConfigEmail)
        throws MessagingException {

        User user = (User) mapConfigEmail.get("user");
        String emailType = (String) mapConfigEmail.get("emailType");

        logger.info("start email configuration for " + emailType + " to : "
            + user.getEmail());
        logger.debug("EmailUtils : User " + user.toString());

        String body = null;
        try {
            mapConfigEmail = this.initEmailConfig(mapConfigEmail);
            mapConfigEmail = this.defineEmailType(mapConfigEmail);
            body = (String) mapConfigEmail.get("body");

            MimeMessage message = (MimeMessage) mapConfigEmail.get("message");

            // For Spring vagrant profil, we redirect all emails
            // If value is not set, we use the classic configuration
            if (applicationContext.getEnvironment().acceptsProfiles("vagrant")
                    && emailForceRedirect.trim().length() > 0) {
                message.setRecipients(Message.RecipientType.TO, emailForceRedirect);
            } else {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            }

            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);

            logger.info("Email of " + emailType + " send to " + user.getEmail());

        } catch(AuthenticationFailedException auex) {
            logger.error("Email of " + emailType + " send to " + user.getEmail(), auex);
        } catch (MessagingException e) {
            logger.error("Error sendEmail method - " + e);
            e.printStackTrace();
        }
    }

    private Map<String, Object> constructModuleInformationsEmail(
        Map<String, Object> mapConfigEmail)
        throws MessagingException {

        String subjectModuleInformationsEmail =
            messageSource.getMessage("mail.subject.module.information", null, Locale.ENGLISH);

        User user = (User) mapConfigEmail.get("user");
        Module module = (Module) mapConfigEmail.get("module");
        Configuration configuration = (Configuration) mapConfigEmail
            .get("configuration");
        MimeMessage message = (MimeMessage) mapConfigEmail.get("message");
        message.setSubject(subjectModuleInformationsEmail + ": "
            + module.getApplication().getName());
        mapConfigEmail.put("message", message);

        logger.info("define Email of module information ");
        logger.debug("constructModuleInformationsBody parameters : User "
            + user.toString() + " Module : " + module);

        Map<String, String> mapVariables = new HashMap<>();
        Map<String, String> moduleInfos = new HashMap<>();

        moduleInfos = module.getModuleInfos();
        mapVariables.put("userLogin", user.getLogin());
        mapVariables.put("userLastName", user.getLastName());
        mapVariables.put("userFirstName", user.getFirstName());

        Template template = null;
        try {
            mapVariables.put("module_seq", module.getInstanceNumber()
                .toString());

            if (module.getName().contains("mysql")) {
                mapVariables.put("mysqlDatabase", moduleInfos.get("database"));
                mapVariables.put("mysqlPort",
                    module.getListPorts().get("mysqlPort"));
                mapVariables.put("mysqlUser", moduleInfos.get("username"));
                mapVariables.put("mysqlPassword", moduleInfos.get("password"));
                mapVariables
                    .put("internalDNSName", module.getInternalDNSName());
                template = configuration
                    .getTemplate("emailModuleInformations-mysql.ftl");

            } else if (module.getName().contains("postgres")) {
                mapVariables.put("pgDatabase", moduleInfos.get("database"));
                mapVariables.put("pgAlias", moduleInfos.get("linkAlias"));
                mapVariables.put("pgPort", module.getListPorts().get("pgPort"));
                mapVariables.put("pgUser", moduleInfos.get("username"));
                mapVariables.put("pgPassword", moduleInfos.get("password"));

                mapVariables
                    .put("internalDNSName", module.getInternalDNSName());

                template = configuration
                    .getTemplate("emailModuleInformations-postgres.ftl");
            } else if (module.getName().contains("mongo")) {
                mapVariables.put("mongoDatabase", moduleInfos.get("database"));
                mapVariables.put("mongoAlias", moduleInfos.get("linkAlias"));
                mapVariables.put("mongoPort",
                    module.getListPorts().get("mongoPort"));
                mapVariables.put("mongoUser", moduleInfos.get("username"));
                mapVariables.put("mongoPassword", moduleInfos.get("password"));
                mapVariables
                    .put("internalDNSName", module.getInternalDNSName());
                template = configuration
                    .getTemplate("emailModuleInformations-mongo.ftl");
            } else if (module.getName().contains("oracle-xe")) {
                mapVariables.put("oracleDatabase", moduleInfos.get("database"));
                mapVariables.put("oracleIP", module.getContainerIP());
                mapVariables.put("oraclePort",
                    module.getListPorts().get("oraclePort"));
                mapVariables.put("oracleUser", moduleInfos.get("username"));
                mapVariables.put("oraclePassword", moduleInfos.get("password"));
                template = configuration
                    .getTemplate("emailModuleInformations-oracle-xe.ftl");
            } else if (module.getName().contains("redis")) {
                mapVariables.put("redisPassword",
                    module.getModuleInfos().get("password"));
                mapVariables
                    .put("internalDNSName", module.getInternalDNSName());
                template = configuration
                    .getTemplate("emailModuleInformations-redis.ftl");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("template : " + template);
            }
        } catch (IOException e) {
            logger.error("Error define Body method : config freemarker " + e);
        }

        mapConfigEmail.put("template", template);
        mapConfigEmail.put("mapVariables", mapVariables);

        logger.info("Variables inject in Email body successfully to "
            + user.getEmail());

        return this.writeBody(mapConfigEmail);

    }

    private Map<String, Object> constructActivationEmail(
        Map<String, Object> mapConfigEmail)
        throws MessagingException {

        String subjectActivationEmail = messageSource.getMessage("mail.subject.activation", null, Locale.ENGLISH);

        Map<String, String> mapVariables = new HashMap<>();

        User user = (User) mapConfigEmail.get("user");
        Configuration configuration = (Configuration) mapConfigEmail
            .get("configuration");
        MimeMessage message = (MimeMessage) mapConfigEmail.get("message");
        message.setSubject(subjectActivationEmail);
        mapConfigEmail.put("message", message);

        logger.info("define Email of activation Email ");
        logger.debug("defineActivationBody parameter : User " + user.toString());

        mapVariables.put("userLogin", user.getLogin());
        mapVariables.put("userLastName", user.getLastName());
        mapVariables.put("userFirstName", user.getFirstName());
        mapVariables.put("userPassword", user.getPassword());
        mapVariables.put("userEmail", user.getEmail());

        Template template = null;
        try {
            template = configuration.getTemplate("emailActivation.ftl");

        } catch (IOException e) {
            logger.error("Error define activation Email's Body : config freemarker "
                + e);
        }
        mapConfigEmail.put("template", template);
        mapConfigEmail.put("mapVariables", mapVariables);

        logger.info("Variables inject in Email's body successfully to "
            + user.getEmail());

        return this.writeBody(mapConfigEmail);
    }

    private Map<String, Object> constructSendPasswordEmail(
        Map<String, Object> mapConfigEmail)
        throws MessagingException {

        String subjectSendPassword = messageSource.getMessage("mail.subject.send.password", null, Locale.ENGLISH);

        Map<String, String> mapVariables = new HashMap<>();

        User user = (User) mapConfigEmail.get("user");
        Configuration configuration = (Configuration) mapConfigEmail
            .get("configuration");
        MimeMessage message = (MimeMessage) mapConfigEmail.get("message");
        message.setSubject(subjectSendPassword);
        mapConfigEmail.put("message", message);

        logger.info("send password ");
        logger.debug("send password : User " + user.toString());

        mapVariables.put("userLogin", user.getLogin());
        mapVariables.put("userPassword", user.getPassword());

        Template template = null;
        try {
            template = configuration.getTemplate("sendPassword.ftl");

        } catch (IOException e) {
            logger.error("Error define sendPassword's Body : config freemarker "
                + e);
        }
        mapConfigEmail.put("template", template);
        mapConfigEmail.put("mapVariables", mapVariables);

        logger.info("Variables inject in Email's body successfully to "
            + user.getEmail());

        return this.writeBody(mapConfigEmail);

    }

    private Map<String, Object> constructChangeEmail(
        Map<String, Object> mapConfigEmail)
        throws MessagingException {

        String subjectChangeEmail = messageSource.getMessage("mail.subject.change.email", null, Locale.ENGLISH);

        Map<String, String> mapVariables = new HashMap<>();

        User user = (User) mapConfigEmail.get("user");
        Configuration configuration = (Configuration) mapConfigEmail
            .get("configuration");
        MimeMessage message = (MimeMessage) mapConfigEmail.get("message");
        message.setSubject(subjectChangeEmail);
        mapConfigEmail.put("message", message);

        logger.info("define Email of change of Email ");
        logger.debug("defineChangeEmailBody parameter : User "
            + user.toString());

        mapVariables.put("userLogin", user.getLogin());
        mapVariables.put("userEmail", user.getEmail());

        Template template = null;
        try {
            template = configuration.getTemplate("emailChangeMail.ftl");

        } catch (IOException e) {
            logger.error("Error define change Email's Body : config freemarker "
                + e);
        }
        mapConfigEmail.put("template", template);
        mapConfigEmail.put("mapVariables", mapVariables);

        logger.info("Variables inject in Email's body successfully to "
            + user.getEmail());

        return this.writeBody(mapConfigEmail);
    }

    /**
     * Write body of Email with freemaker template and variables chosen before
     *
     * @param mapConfigEmail
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> writeBody(Map<String, Object> mapConfigEmail) {
        String body;
        Template template = (Template) mapConfigEmail.get("template");
        template.setEncoding("UTF-8");
        String htmlFile = template.getName().replace("ftl", "html");
        FileWriter writer = null;
        File file;
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;

        Map<String, String> mapVariables = (Map<String, String>) mapConfigEmail
            .get("mapVariables");

        try {
            file = File.createTempFile(htmlFile, "email");
            writer = new FileWriter(file);
            template.process(mapVariables, writer);
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine());

            }

            bufferedReader.close();
            file.delete();

        } catch (IOException | TemplateException e) {
            logger.error("Error constructBody method : IO issue : " + e);
            e.printStackTrace();
        }
        body = stringBuilder.toString();
        mapConfigEmail.put("body", body);

        return mapConfigEmail;

    }

}

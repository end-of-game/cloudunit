package fr.treeptik.cloudunitmonitor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
@PropertySource({ "classpath:config.properties" })
public class EmailUtils {

	@Inject
	private Environment env;

	private Logger logger = LoggerFactory.getLogger(EmailUtils.class);

	/**
	 * Store general propreties (mailJet), create session and addressFrom
	 * 
	 * @param mapConfigEmail
	 * @return
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 */
	private Map<String, Object> initEmailConfig(
			Map<String, Object> mapConfigEmail) throws AddressException,
			MessagingException, IOException {

		final String apiKey = env.getProperty("mail.apiKey");
		final String secretKey = env.getProperty("mail.secretKey");
		String emailFrom = env.getProperty("mail.emailFrom");
		String mailSmtpHost = env.getProperty("mail.smtpHost");
		String socketFactoryPort = env.getProperty("mail.socketFactoryPort");
		String smtpPort = env.getProperty("mail.smtpPort");

		Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(this.getClass(),
				"/fr.treeptik.cloudunitmonitor.templates/");
		Template template = configuration.getTemplate("sendErrorsToAdmin.ftl");
		mapConfigEmail.put("template", template);

		Properties props = new Properties();
		props.put("mail.smtp.host", mailSmtpHost);
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
		logger.info(mapConfigEmail.toString());
		return mapConfigEmail;

	}

	/**
	 * public method to send mail parameter is map with emailType, user
	 * 
	 * @param mapConfigEmail
	 * @throws MessagingException
	 * @throws IOException
	 */
	@Async
	public void sendEmail(String errorsMessage) throws MessagingException,
			IOException {

		Map<String, Object> mapConfigEmail = new HashMap<>();
		Map<String, String> mapVariables = new HashMap<>();
		mapVariables.put("errorsMessage", errorsMessage);
		mapVariables.put("instance", ApplicationEntryPoint.instanceName);

		mapConfigEmail.put("mapVariables", mapVariables);

		String body = null;
		try {
			logger.info("entree send email");
			mapConfigEmail = this.initEmailConfig(mapConfigEmail);
			mapConfigEmail = this.writeBody(mapConfigEmail);

			body = (String) mapConfigEmail.get("body");

			MimeMessage message = (MimeMessage) mapConfigEmail.get("message");
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(env.getProperty("support.email")));
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

		} catch (MessagingException e) {
			logger.error("Error sendEmail method - " + e);
			e.printStackTrace();
		}

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
		String htmlFile = template.getName().replace("ftl", "html");
		FileWriter writer = null;
		File file;
		StringBuilder stringBuilder = new StringBuilder();
		FileReader fileReader = null;

		try {
			file = File.createTempFile(htmlFile, "email");
			writer = new FileWriter(file);
			template.process(
					((Map<String, String>) mapConfigEmail.get("mapVariables")),
					writer);
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
		logger.info(mapConfigEmail.toString());

		return mapConfigEmail;

	}
}

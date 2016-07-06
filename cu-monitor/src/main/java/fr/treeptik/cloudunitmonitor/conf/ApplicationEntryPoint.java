package fr.treeptik.cloudunitmonitor.conf;

import java.io.IOException;
import java.net.InetAddress;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.treeptik.cloudunitmonitor.exception.ServiceException;
import fr.treeptik.cloudunitmonitor.service.HealthMonitor;

public class ApplicationEntryPoint {

	public static String instanceName = "";

	private static Logger logger = LoggerFactory.getLogger(ApplicationEntryPoint.class);

	public static String IP_MYSQL = null;
	public static String IP_REDIS = null;
	public static String MYSQL_PASSWORD = null;
	public static String MODE = null;

	public static void main(String[] args)
			throws ServiceException, MessagingException, IOException, InterruptedException {

		IP_MYSQL = args[0];
		MYSQL_PASSWORD = args[1];
		IP_REDIS = args[2];
		MODE = args[3];

		logger.info("First argument (ip mysql) : " + IP_MYSQL);
		logger.info("Second argument (password mysql) : " + MYSQL_PASSWORD);
		logger.info("Third argument (ip redis) : " + IP_REDIS);
		logger.info("Four argument (mode) : " + MODE);

		if (args.length < 4) {
			logger.error("One parameter is missing : ");
			System.exit(1);
		}

		instanceName = InetAddress.getLocalHost().getHostName();

		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(
				ApplicationConfiguration.class);
		HealthMonitor healthMonitor = (HealthMonitor) annotationConfigApplicationContext.getBean("healthMonitor");

		logger.debug("****Checking containers****");

		int status = 0;
		for (int i = 0; i < 5; i++) {

			Thread.sleep(1000);

			int realStatus = healthMonitor.checkAllServersAndModules();

			if (realStatus > status) {
				status = realStatus;
			}
		}

		System.exit(status);

	}
}

package fr.treeptik.cloudunit.config.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class CustomHttpSessionListener implements HttpSessionListener {

	private Logger logger = LoggerFactory
			.getLogger(CustomHttpSessionListener.class);

	public void sessionCreated(HttpSessionEvent sessionEvent) {
		logger.info("Session Created:: ID=" + sessionEvent.getSession().getId());
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		logger.info("Session Destroyed:: ID="
				+ sessionEvent.getSession().getId());
	}
}

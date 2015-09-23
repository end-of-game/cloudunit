package fr.treeptik.cloudunit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Returns a 401 error code (Unauthorized) to the client, when Ajax
 * authentication fails.
 */
@Component
public class UserAjaxAuthenticationFailureHandler extends
		SimpleUrlAuthenticationFailureHandler {

	private Logger logger = LoggerFactory
			.getLogger(UserAjaxAuthenticationFailureHandler.class);

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		logger.warn("Authentication failed");
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				"Authentication failed");
	}
}

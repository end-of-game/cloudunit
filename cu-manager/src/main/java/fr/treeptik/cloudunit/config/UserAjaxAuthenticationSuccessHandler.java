package fr.treeptik.cloudunit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
public class UserAjaxAuthenticationSuccessHandler extends
		SimpleUrlAuthenticationSuccessHandler {

	private Logger logger = LoggerFactory
			.getLogger(UserAjaxAuthenticationSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		logger.info("SC_OK");
		response.setStatus(HttpServletResponse.SC_OK);
	}
}

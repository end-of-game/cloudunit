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

package fr.treeptik.cloudunit.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Returns a 401 error code (Unauthorized) to the client.
 */
@Component
public class Http401EntryPoint implements AuthenticationEntryPoint {

	private final Logger log = LoggerFactory.getLogger(Http401EntryPoint.class);

	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg)
			throws IOException, ServletException {

		// Maybe change the log level...
		log.warn("Access Denied [ " + request.getRequestURL().toString() + "] : " + arg.getMessage());

		// Trace message to ban intruders with fail2ban
		// generateLogTraceForFail2ban();

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access unauthorized");
	}

	public void generateLogTraceForFail2ban() {
		log.debug("generateLogTraceForFail2ban");
		String filePath = "/var/log/culogin.log";
		try {
			Files.write(Paths.get(filePath), "Access Denied".getBytes(), StandardOpenOption.APPEND);
			Files.write(Paths.get(filePath), System.getProperty("line.separator").getBytes(),
					StandardOpenOption.APPEND);
		} catch (IOException e) {
			log.error("Cannot write to " + filePath + "", e.getMessage());
		}
	}
}

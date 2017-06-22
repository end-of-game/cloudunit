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
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.dao.RoleDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Role;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
public class UserAjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleDAO roleDao;

	private Logger logger = LoggerFactory.getLogger(UserAjaxAuthenticationSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		/*************************************************************************/
		LdapUserDetailsImpl principal = (LdapUserDetailsImpl) authentication.getPrincipal();
		String username = principal.getUsername();
		try {
			User user = userService.findByLogin(username);
			if (user == null) {
				Role role = roleDao.findOne(2);
				user = new User(null, "anne", "alyse", "treeptik", new Date(), "m.grillet@treeptik.fr",
						"cVwsWoHVZ28Qf9fHE0W4Qg==", 1, role, null);
				user.setLogin(username);
				userService.create(user);
			}
		} catch (ServiceException e) {
			logger.debug(e.getMessage(), e);
		}
		/*************************************************************************/

		logger.info("SC_OK");
		response.setStatus(HttpServletResponse.SC_OK);
	}
}

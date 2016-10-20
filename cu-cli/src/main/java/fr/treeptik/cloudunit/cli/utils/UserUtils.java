/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.cli.utils;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import jline.console.ConsoleReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UserUtils {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthenticationUtils authentificationUtils;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	@Autowired
	private FileUtils fileUtils;

	public String changePassword() {

		String oldPassword, newPassword, newPassword2 = "";

		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (fileUtils.isInFileExplorer()) {
			log.log(Level.SEVERE,
					"You are currently in a container file explorer. Please exit it with close-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}
		try {
			log.log(Level.INFO, "Enter your old password : ");
			oldPassword = new ConsoleReader().readLine(new Character('*'));
			log.log(Level.INFO, "Enter your new password : ");
			newPassword = new ConsoleReader().readLine(new Character('*'));
			log.log(Level.INFO, "Please confirm your new password : ");
			newPassword2 = new ConsoleReader().readLine(new Character('*'));

			if (!newPassword2.equalsIgnoreCase(newPassword)) {
				log.log(Level.SEVERE, "The password confirmation is incorrect! Please retry!");
				return null;
			}

			Map<String, String> parameters = new HashMap<>();

			parameters.put("password", oldPassword);
			parameters.put("newPassword", newPassword);
			restUtils.sendPutCommand(authentificationUtils.finalHost + "/user/change-password",
					authentificationUtils.getMap(), parameters);
			log.log(Level.INFO, "Your password was successfully changed");

			statusCommand.setExitStatut(0);
		} catch (ResourceAccessException e) {
			log.log(Level.SEVERE,
					"The CLI can't etablished connexion with host servers. Please try later or contact an admin");
			statusCommand.setExitStatut(1);
			return null;
		} catch (Exception e) {
			statusCommand.setExitStatut(1);
			return null;
		}
		return null;
	}
}

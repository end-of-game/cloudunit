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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;

@Component
public class AdminUtils {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthentificationUtils authentificationUtils;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	@Autowired
	private FileUtils fileUtils;

	public String listUsers() {

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
			MessageConverter.buildListUsers(JsonConverter.getUsers(restUtils
					.sendGetCommand(authentificationUtils.finalHost + "/admin/users", authentificationUtils.getMap())
					.get("body")));
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

	public String createUser(String login, String firstName, String lastName, String organization, String email,
			String password) {
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

			Map<String, String> parameters = new HashMap<>();
			parameters.put("login", login);
			parameters.put("firstName", firstName);
			parameters.put("lastName", lastName);
			parameters.put("organization", organization);
			parameters.put("email", email);
			parameters.put("password", password);
			restUtils.sendPostCommand(authentificationUtils.finalHost + "/admin/user", authentificationUtils.getMap(),
					parameters);
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
		return "The user " + login + " was successfully added";
	}

	public String changeRightsUser(String login, String role) {
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
			Map<String, String> parameters = new HashMap<>();
			parameters.put("login", login);
			parameters.put("role", role);
			restUtils.sendPostCommand(authentificationUtils.finalHost + "/admin/user/rights",
					authentificationUtils.getMap(), parameters);
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
		return login + " is now an " + role.toUpperCase();
	}

	public String deleteUser(String login) {
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
			restUtils.sendDeleteCommand(authentificationUtils.finalHost + "/admin/user/" + login,
					authentificationUtils.getMap());
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
		return "The user " + login + " was successfully removed";
	}

	public String getMessages(String login, String rows) {
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

		if (rows == null || rows.isEmpty()) {
			rows = "15";
		}
		try {
			Integer.parseInt(rows);
		} catch (NumberFormatException e) {
			log.log(Level.SEVERE, "The row number is incorrect");
			return null;
		}
		try {
			MessageConverter
					.buildUserMessages(
							JsonConverter
									.getMessage(restUtils
											.sendGetCommand(authentificationUtils.finalHost + "/admin/messages/rows/"
													+ rows + "/login/" + login, authentificationUtils.getMap())
											.get("body")));

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

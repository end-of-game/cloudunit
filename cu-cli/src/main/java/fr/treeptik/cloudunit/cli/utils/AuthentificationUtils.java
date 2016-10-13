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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.cli.shell.CloudUnitPromptProvider;
import jline.console.ConsoleReader;

@Component
public class AuthentificationUtils {

	public String finalHost;
	@InjectLogger
	private Logger log;
	private Map<String, Object> map = new HashMap<>();
	@Value("${host}")
	private String defaultHost;
	@Value("${manager.version}")
	private String apiVersion;
	@Autowired
	private UrlLoader urlLoader;
	@Autowired
	private CloudUnitPromptProvider clPromptProvider;
	@Autowired
	private ShellStatusCommand statusCommand;
	@Autowired
	private RestUtils restUtils;
	@Autowired
	private ApplicationUtils applicationUtils;
	@Autowired
	private FileUtils fileUtils;
	private Integer loop = 0;

	public void checkConnected() {
	    if (!isConnected()) {
	        throw new CloudUnitCliException("You are not connected to a CloudUnit host. Use the 'connect' command first.");
	    }
	}
	
	public boolean isConnected() {
	    return !map.isEmpty();
	}
	
	/**
	 * Methode de connexion
	 *
	 * @param login
	 * @param password
	 * @param selectedHost
	 * @return
	 */
	public String connect(String login, String password, String selectedHost) {

		if (!map.isEmpty()) {
			statusCommand.setExitStatut(0);
			return (ANSIConstants.ANSI_PURPLE + "You are already connected to CloudUnit servers"
					+ ANSIConstants.ANSI_RESET);
		}

		if (fileUtils.isInFileExplorer()) {
			statusCommand.setExitStatut(1);
			return (ANSIConstants.ANSI_RED
					+ "You are currently in a container file explorer. Please exit it with close-explorer command"
					+ ANSIConstants.ANSI_RESET);

		}

		try {
			loop++;

			if (password.equalsIgnoreCase("")) {
				log.log(Level.INFO, "Enter your password : ");
				try {
					password = new ConsoleReader().readLine(new Character('*'));
				} catch (IOException e) {
					return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
				}
			}
			Map<String, Object> loginInfo = new HashMap<>();
			loginInfo.put("login", login);
			loginInfo.put("password", password);

			// check the host

			if (!selectedHost.isEmpty()) {
				log.log(Level.INFO, "Trying to connect to " + selectedHost);
				finalHost = selectedHost;

			} else {
				log.log(Level.INFO, "Trying to connect to default CloudUnit host...");
				finalHost = defaultHost;
			}

			// trying to connect with host manager
			String urlToCall = finalHost + urlLoader.connect;
			restUtils.connect(urlToCall, loginInfo).get("body");
			applicationUtils.setApplication(null);

			String response = null;
			String cloudunitInstance = "";
			try {
				response = restUtils.sendGetCommand(finalHost + urlLoader.getCloudUnitInstance, null).get("body");
				cloudunitInstance = JsonConverter.getCloudUnitInstance(response);
				if (cloudunitInstance != null) {
					cloudunitInstance = "-" + cloudunitInstance;
				}
				clPromptProvider.setCuInstanceName(cloudunitInstance);
			} catch (ManagerResponseException e) {
				statusCommand.setExitStatut(1);
				return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
			}

		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			if (loop >= 3) {
				return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
			}

			password = "";
			return this.connect(login, password, selectedHost);
		}

		map.put("login", login);
		map.put("password", password);
		loop = 0;

		statusCommand.setExitStatut(0);
		return "Connection established";

	}

	/**
	 * Appel de l'url spring-secu pour suppression session côté serveur
	 */
	public String disconnect() {
		if (fileUtils.isInFileExplorer()) {

			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED
					+ "You are currently in a container file explorer. Please exit it with close-explorer command"
					+ ANSIConstants.ANSI_RESET;
		}
		try {
			restUtils.sendGetCommand(finalHost + "/user/logout", map);
			map.clear();
			applicationUtils.setApplication(null);
			restUtils.localContext = null;
		} catch (ManagerResponseException e) {
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		clPromptProvider.setCuInstanceName("");

		return "Disconnect";

	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

}

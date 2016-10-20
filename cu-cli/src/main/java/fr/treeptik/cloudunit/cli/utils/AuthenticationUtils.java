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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.Guard;
import fr.treeptik.cloudunit.cli.Messages;
import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;

@Component
public class AuthenticationUtils {
    private static final String NOT_CONNECTED = Messages.getString("auth.NOT_CONNECTED");
    private static final String ALREADY_CONNECTED = Messages.getString("auth.ALREADY_CONNECTED");

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
	private ShellStatusCommand statusCommand;
	@Autowired
	private RestUtils restUtils;
	@Autowired
	private ApplicationUtils applicationUtils;
	@Autowired
	private FileUtils fileUtils;
	
	private String currentInstanceName;

	public void checkConnected() {
	    Guard.guardTrue(isConnected(), NOT_CONNECTED);
	}
	
	public void checkNotConnected() {
        Guard.guardTrue(!isConnected(), ALREADY_CONNECTED);
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
	public String connect(String login, String password, String selectedHost, Prompter prompter) {
	    checkNotConnected();
		fileUtils.checkNotInFileExplorer();
		
		String finalPassword = password;
        
        if (password.isEmpty()) {
            finalPassword = prompter.promptPassword("Enter password:");
        }
		
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("login", login);
        loginInfo.put("password", finalPassword);

        if (selectedHost.isEmpty()) {
            finalHost = defaultHost;
        } else {
            finalHost = selectedHost;
        }
        log.log(Level.INFO, MessageFormat.format("Connecting to {0}...", finalHost));

        try {
            // trying to connect with host manager
			String urlToCall = finalHost + urlLoader.connect;
			restUtils.connect(urlToCall, loginInfo).get("body");
			applicationUtils.setCurrentApplication(null);

			String response = null;

            response = restUtils.sendGetCommand(finalHost + urlLoader.getCloudUnitInstance, null).get("body");
            String cloudunitInstance = JsonConverter.getCloudUnitInstance(response);
            currentInstanceName = cloudunitInstance != null ? cloudunitInstance : "";
		} catch (ManagerResponseException e) {
		    statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		map.put("login", login);
		map.put("password", finalPassword);

		statusCommand.setExitStatut(0);
		return "Connection established";
	}

	/**
	 * Appel de l'url spring-secu pour suppression session côté serveur
	 */
	public String disconnect() {
	    checkConnected();
		fileUtils.checkNotInFileExplorer();
		
		try {
			restUtils.sendGetCommand(finalHost + "/user/logout", map);
			map.clear();
			applicationUtils.setCurrentApplication(null);
			restUtils.localContext = null;
		} catch (ManagerResponseException e) {
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		
		currentInstanceName = null;

		return "Disconnected";
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

    public String getCurrentInstanceName() {
        return currentInstanceName;
    }

}

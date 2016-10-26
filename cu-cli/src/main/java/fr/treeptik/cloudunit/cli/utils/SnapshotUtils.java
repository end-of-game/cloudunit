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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.model.Snapshot;

@Component
public class SnapshotUtils {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthenticationUtils authentificationUtils;

	@Autowired
	private ApplicationUtils applicationUtils;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private FileUtils fileUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	public String createSnapshot(String tag, String applicationName) {

		String checkResponse = applicationUtils.checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		if (applicationName != null) {
			applicationUtils.useApplication(applicationName);
		} else {
			applicationName = applicationUtils.getCurrentApplication().getName();
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationName);
		parameters.put("tag", tag);

		try {
			restUtils.sendPostCommand(authentificationUtils.finalHost + "/snapshot", authentificationUtils.getMap(),
					parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		return "A new snapshot called " + tag + " was successfully created.";
	}

	public String deleteSnapshot(String tag) {

		if (authentificationUtils.getMap().isEmpty()) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + "You are not connected to CloudUnit host! Please use connect command"
					+ ANSIConstants.ANSI_RESET;
		}
		if (fileUtils.isInFileExplorer()) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED
					+ "You are currently in a container file explorer. Please exit it with close-explorer command"
					+ ANSIConstants.ANSI_RESET;
		}

		try {
			restUtils.sendDeleteCommand(authentificationUtils.finalHost + "/snapshot/" + tag,
					authentificationUtils.getMap()).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		return "The snapshot " + tag + " was successfully deleted.";
	}

	public String listAllSnapshots() {
		List<Snapshot> listSnapshots;
		String json = null;

		if (authentificationUtils.getMap().isEmpty()) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + "You are not connected to CloudUnit host! Please use connect command"
					+ ANSIConstants.ANSI_RESET;
		}

		if (fileUtils.isInFileExplorer()) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED
					+ "You are currently in a container file explorer. Please exit it with close-explorer command"
					+ ANSIConstants.ANSI_RESET;
		}
		try {
			json = (String) restUtils
					.sendGetCommand(authentificationUtils.finalHost + "/snapshot/list", authentificationUtils.getMap())
					.get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		listSnapshots = JsonConverter.getSnapshot(json);
		MessageConverter.buildListSnapshots(listSnapshots);
		statusCommand.setExitStatut(0);
		return listSnapshots.size() + " snapshots found";
	}

	public String clone(String applicationName, String tag) {
		if (authentificationUtils.getMap().isEmpty()) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + "You are not connected to CloudUnit host! Please use connect command"
					+ ANSIConstants.ANSI_RESET;
		}
		if (fileUtils.isInFileExplorer()) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED
					+ "You are currently in a container file explorer. Please exit it with close-explorer command"
					+ ANSIConstants.ANSI_RESET;
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationName);
		parameters.put("tag", tag);
		try {
			restUtils.sendPostCommand(authentificationUtils.finalHost + "/snapshot/clone",
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);
		return "Your application " + applicationName + " was successfully created.";
	}

}

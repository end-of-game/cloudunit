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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.RestUtils;

@Component
public class ServerUtils implements CommandMarker {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthentificationUtils authentificationUtils;

	@Autowired
	private ApplicationUtils applicationUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	@Autowired
	private RestUtils restUtils;

	private List<String> availableJavaVersion = Arrays.asList(new String[] { "jdk1.7.0_55", "jdk1.8.0_25" });

	private List<String> availableMemoryValues = Arrays.asList("512", "1024", "2048", "3072");

	/**
	 * @param memory
	 * @return
	 */
	public String changeMemory(String memory) {
		String checkResponse = applicationUtils.checkAndRejectIfError(null);
		if (checkResponse != null) {
			return checkResponse;
		}

		if (!availableMemoryValues.contains(memory)) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + "The memory value you have put is not authorized (512, 1024, 2048, 3072)"
					+ ANSIConstants.ANSI_RESET;
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getApplication().getName());
		parameters.put("jvmMemory", memory);
		parameters.put("jvmRelease", applicationUtils.getApplication().getJvmRelease());
		parameters.put("jvmOptions", applicationUtils.getApplication().getServer().getJvmOptions().toString());
		try {
			restUtils.sendPutCommand(authentificationUtils.finalHost + "/server/configuration/jvm",
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		applicationUtils.useApplication(applicationUtils.getApplication().getName());

		statusCommand.setExitStatut(0);

		return "Change memory on " + applicationUtils.getApplication().getName() + " successful";
	}

	/**
	 * Add an option for JVM
	 *
	 * @param opts
	 * @return
	 */
	public String addOpts(String opts) {

		String checkResponse = applicationUtils.checkAndRejectIfError(null);
		if (checkResponse != null) {
			return checkResponse;
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getApplication().getName());
		parameters.put("jvmOptions", opts);
		parameters.put("jvmRelease", applicationUtils.getApplication().getJvmRelease());
		parameters.put("jvmMemory", applicationUtils.getApplication().getServer().getJvmMemory().toString());
		try {
			restUtils.sendPutCommand(authentificationUtils.finalHost + "/server/configuration/jvm",
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		applicationUtils.useApplication(applicationUtils.getApplication().getName());

		statusCommand.setExitStatut(0);

		return "Add java options to " + applicationUtils.getApplication().getName() + " application successfully";
	}

	/**
	 * Change JVM Release
	 *
	 * @param applicationName
	 * @param jvmRelease
	 * @return
	 */
	public String changeJavaVersion(String applicationName, String jvmRelease) {

		String checkResponse = applicationUtils.checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		if (applicationName != null) {
			applicationUtils.useApplication(applicationName);
		} else {
			applicationName = applicationUtils.getApplication().getName();
		}

		if (!availableJavaVersion.contains(jvmRelease)) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + "The specified java version is not available" + ANSIConstants.ANSI_RESET;
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getApplication().getName());
		parameters.put("jvmRelease", jvmRelease);
		parameters.put("jvmMemory", applicationUtils.getApplication().getServer().getJvmMemory().toString());
		parameters.put("jvmOptions", applicationUtils.getApplication().getServer().getJvmOptions().toString());
		try {
			restUtils.sendPutCommand(authentificationUtils.finalHost + "/server/configuration/jvm",
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		applicationUtils.useApplication(applicationUtils.getApplication().getName());
		statusCommand.setExitStatut(0);

		return "Your java version has been successfully changed";
	}

	/**
	 * TODO
	 *
	 * @param applicationName
	 * @param portToOpen
	 * @return
	 */
	public String openPort(String applicationName, String portToOpen, String portNature) {

		String checkResponse = applicationUtils.checkAndRejectIfError(applicationName);

		if (checkResponse != null) {
			return checkResponse;
		}

		if (applicationName != null) {
			applicationUtils.useApplication(applicationName);
		} else {
			applicationName = applicationUtils.getApplication().getName();
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getApplication().getName());
		parameters.put("portToOpen", portToOpen);
		parameters.put("portNature", portNature);

		try {
			restUtils.sendPostCommand(authentificationUtils.finalHost + "/application/ports",
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);

		return "The port " + portToOpen + " was been successfully opened on "
				+ applicationUtils.getApplication().getName();
	}

	/**
	 * @param applicationName
	 * @param portToOpen
	 * @return
	 */
	public String removePort(String applicationName, String portToOpen) {

		String checkResponse = applicationUtils.checkAndRejectIfError(applicationName);

		if (checkResponse != null) {
			return checkResponse;
		}

		if (applicationName != null) {
			applicationUtils.useApplication(applicationName);
		} else {
			applicationName = applicationUtils.getApplication().getName();
		}

		try {
			restUtils.sendDeleteCommand(
					authentificationUtils.finalHost + "/application/" + applicationName + "/ports/" + portToOpen,
					authentificationUtils.getMap()).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);

		return "The port " + portToOpen + " was been successfully closed on "
				+ applicationUtils.getApplication().getName();
	}

}

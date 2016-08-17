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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.model.Module;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.RestUtils;

@Component
public class ModuleUtils {

	@Autowired
	private ApplicationUtils applicationUtils;

	@Autowired
	private AuthentificationUtils authentificationUtils;

	@Autowired
	private CheckUtils checkUtils;

	@Autowired
	private UrlLoader urlLoader;

	@InjectLogger
	private Logger log;

	@Autowired
	private ShellStatusCommand statusCommand;

	@Autowired
	private RestUtils restUtils;

	private String applicationName;

	public String getListModules() {
		String checkResponse = applicationUtils.checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}
		String dockerManagerIP = applicationUtils.getApplication().getManagerIp();
		statusCommand.setExitStatut(0);
		MessageConverter.buildLightModuleMessage(applicationUtils.getApplication(), dockerManagerIP);

		return applicationUtils.getApplication().getModules().size() + " modules found";
	}

	public String addModule(final String imageName, final File script) {
		String response = null;
		String checkResponse = applicationUtils.checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}
		Map<String, String> parameters = new HashMap<>();
		parameters.put("imageName", imageName);
		parameters.put("applicationName", applicationName);
		try {
			if (checkUtils.checkImageNoExist(imageName)) {
				return "this module does not exist";
			}
			restUtils.sendPostCommand(authentificationUtils.finalHost + urlLoader.modulePrefix,
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		statusCommand.setExitStatut(0);
		applicationName = applicationUtils.getApplication().getName();
		applicationUtils.useApplication(applicationName);
		response = "Your module " + imageName + " is currently being added to your application "
				+ applicationUtils.getApplication().getName();

		return response;

	}

	public String removeModule(String moduleName) {
		String checkResponse = applicationUtils.checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		if (applicationUtils.getApplication().getModules().size() == 0) {
			return "The application " + applicationUtils.getApplication().getName() + " doesn't have any module.";
		}

		Boolean exists = false;
		for (Module module : applicationUtils.getApplication().getModules()) {
			if (module.getName().endsWith(moduleName)) {
				exists = true;
			}
		}

		if (exists == false) {
			return "The application " + applicationUtils.getApplication().getName() + " doesn't have this module.";
		}

		for (Module module : applicationUtils.getApplication().getModules()) {

			if (module.getName().endsWith(moduleName)) {
				try {
					restUtils.sendDeleteCommand(
							authentificationUtils.finalHost + urlLoader.modulePrefix
									+ applicationUtils.getApplication().getName() + "/" + module.getName(),
							authentificationUtils.getMap()).get("body");
				} catch (ManagerResponseException e) {
					statusCommand.setExitStatut(1);
					return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
				}
			}
		}

		// update the current application
		applicationName = applicationUtils.getApplication().getName();
		applicationUtils.useApplication(applicationName);

		return "Your module " + moduleName + " is currently being removed from your application "
				+ applicationUtils.getApplication().getName();

	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

}

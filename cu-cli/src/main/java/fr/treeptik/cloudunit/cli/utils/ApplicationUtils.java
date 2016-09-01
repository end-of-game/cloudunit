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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.cli.shell.CloudUnitPromptProvider;
import fr.treeptik.cloudunit.model.Application;
import jline.console.ConsoleReader;

@Component
public class ApplicationUtils {

	@InjectLogger
	private Logger log;

	@Autowired
	private UrlLoader urlLoader;

	@Autowired
	private AuthentificationUtils authentificationUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private CheckUtils checkUtils;

	@Autowired
	private ModuleUtils moduleUtils;

	@Autowired
	private CloudUnitPromptProvider clPromptProvider;

	@Autowired
	private FileUtils fileUtils;

	private Application application;

	public String getInformations() {
		String checkResponse = checkAndRejectIfError(null);
		if (checkResponse != null) {
			return checkResponse;
		}

		useApplication(application.getName());
		String dockerManagerIP = application.getManagerIp();
		statusCommand.setExitStatut(0);

		MessageConverter.buildApplicationMessage(application, dockerManagerIP);
		return "Terminated";
	}

	public String useApplication(String applicationName) {
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
			json = restUtils
					.sendGetCommand(authentificationUtils.finalHost + urlLoader.actionApplication + applicationName,
							authentificationUtils.getMap())
					.get("body");
		} catch (ManagerResponseException e) {
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		statusCommand.setExitStatut(0);

		moduleUtils.setApplicationName(applicationName);
		setApplication(JsonConverter.getApplication(json));
		clPromptProvider.setApplicationName("-" + applicationName);
		return "Current application : " + getApplication().getName();
	}

	public String createApp(String applicationName, String serverName) {
		String response = null;
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
			if (checkUtils.checkImageNoExist(serverName)) {
				statusCommand.setExitStatut(1);
				return ANSIConstants.ANSI_RED + "This server image does not exist" + ANSIConstants.ANSI_RESET;
			}
			Map<String, String> parameters = new HashMap<>();
			parameters.put("applicationName", applicationName);
			parameters.put("serverName", serverName);

			restUtils.sendPostCommand(authentificationUtils.finalHost + urlLoader.actionApplication,
					authentificationUtils.getMap(), parameters).get("body");

			statusCommand.setExitStatut(0);

			response = "Your application " + applicationName + " is currently being installed";

		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		useApplication(applicationName);

		return response;
	}

	public String rmApp(String applicationName, Boolean scriptUsage) {

		// Check if application is eligble
		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			statusCommand.setExitStatut(1);
			return checkResponse;
		}

		// Enter the non interactive mode (for script)
		String response = null;
		if (scriptUsage) {
			try {
				restUtils.sendDeleteCommand(
						authentificationUtils.finalHost + urlLoader.actionApplication + application.getName(),
						authentificationUtils.getMap()).get("body");
				response = "Your application " + application.getName() + " is currently being removed";
				statusCommand.setExitStatut(0);
			} catch (ResourceAccessException e) {
				statusCommand.setExitStatut(1);
				response = ANSIConstants.ANSI_RED
						+ "The CLI can't etablished connexion with host servers. Please try later or contact an admin"
						+ ANSIConstants.ANSI_RESET;
			} catch (ManagerResponseException e) {
				statusCommand.setExitStatut(1);
				response = ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
			} finally {
				clPromptProvider.setApplicationName("");
				setApplication(null);
			}
			return response;
		}

		// For human interactive mode
		try {
			log.log(Level.WARNING,
					"Confirm the suppression of your application: " + application.getName() + " - (yes/y) or (no/n)");
			String confirmation = new ConsoleReader().readLine();
			confirmation = confirmation.toLowerCase();

			switch (confirmation) {
			case "yes":
			case "y":
				restUtils.sendDeleteCommand(
						authentificationUtils.finalHost + urlLoader.actionApplication + application.getName(),
						authentificationUtils.getMap()).get("body");

				response = "Your application " + application.getName() + " is currently being removed";
				statusCommand.setExitStatut(0);
				break;

			case "no":
			case "n":
				statusCommand.setExitStatut(0);
				break;
			default:
				log.log(Level.SEVERE, "Confirmation response is (yes/y) or (no/n) ");
				return rmApp(applicationName, scriptUsage);
			}
		} catch (ResourceAccessException e) {
			statusCommand.setExitStatut(1);
			response = ANSIConstants.ANSI_RED
					+ "The CLI can't etablished connexion with host servers. Please try later or contact an admin"
					+ ANSIConstants.ANSI_RESET;
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			response = ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		} catch (IOException e) {
			statusCommand.setExitStatut(1);
			response = ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		} finally {
			setApplication(null);
			clPromptProvider.setApplicationName("");
		}

		return response;
	}

	public String startApp(String applicationName) {
		String response = null;

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put("applicationName", application.getName());
			restUtils.sendPostCommand(authentificationUtils.finalHost + urlLoader.actionApplication + urlLoader.start,
					authentificationUtils.getMap(), parameters).get("body");
			response = "Your application " + application.getName().toLowerCase() + " is currently being started";
			statusCommand.setExitStatut(0);

		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		return response;
	}

	public String stopApp(String applicationName) {

		String response = null;

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", application.getName());

		try {
			restUtils.sendPostCommand(authentificationUtils.finalHost + urlLoader.actionApplication + urlLoader.stop,
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		response = "Your application " + application.getName().toLowerCase() + " is currently being stopped";
		statusCommand.setExitStatut(0);

		return response;
	}

	public List<Application> listAllApps() throws ManagerResponseException {
		List<Application> listApplications;
		String json = null;

		try {
			json = (String) restUtils.sendGetCommand(authentificationUtils.finalHost + urlLoader.listAllApplications,
					authentificationUtils.getMap()).get("body");
		} catch (ManagerResponseException e) {
			throw new ManagerResponseException(e.getMessage(), e);
		}

		listApplications = JsonConverter.getApplications(json);
		statusCommand.setExitStatut(0);
		return listApplications;
	}

	public String listAll() {
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

		List<Application> listApplications = null;
		try {
			listApplications = listAllApps();
		} catch (ManagerResponseException e) {
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		if (listApplications != null) {
			MessageConverter.buildListApplications(listApplications);
		}
		return listApplications.size() + " found !";
	}

	public String deployFromAWar(File path, boolean openBrowser) throws MalformedURLException, URISyntaxException {

		String checkResponse = checkAndRejectIfError(null);
		String body = "";
		if (checkResponse != null) {
			return checkResponse;
		}

		if (path == null) {

			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + "You must precise the file path with -p option" + ANSIConstants.ANSI_RESET;

		} else {
			// refresh application informations
			useApplication(application.getName());

			try {
				File file = path;
				FileInputStream fileInputStream = new FileInputStream(file);
				fileInputStream.available();
				fileInputStream.close();
				FileSystemResource resource = new FileSystemResource(file);
				Map<String, Object> params = new HashMap<>();
				params.put("file", resource);
				params.putAll(authentificationUtils.getMap());
				body = (String) restUtils.sendPostForUpload(authentificationUtils.finalHost
						+ urlLoader.actionApplication + application.getName() + "/deploy", params).get("body");
				statusCommand.setExitStatut(0);

			} catch (IOException e) {

				return ANSIConstants.ANSI_RED + "File not found! Check the path file" + ANSIConstants.ANSI_RESET;
			}
		}

		if (!body.equalsIgnoreCase("") && openBrowser) {
			DesktopAPI.browse(new URL(application.getLocation()).toURI());
		}

		return "War deployed - Access on " + application.getLocation();
	}

	public String addNewAlias(String applicationName, String alias) {
		String response = null;

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", application.getName());
		parameters.put("alias", alias);
		try {
			restUtils.sendPostCommand(authentificationUtils.finalHost + urlLoader.actionApplication + "/alias",
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		statusCommand.setExitStatut(0);
		response = "An alias has been successfully added to " + application.getName();

		return response;
	}

	public String listAllAliases(String applicationName) {
		String response = null;

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		try {
			response = restUtils.sendGetCommand(
					authentificationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/alias",
					authentificationUtils.getMap()).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		MessageConverter.buildListAliases(JsonConverter.getAliases(response));

		statusCommand.setExitStatut(0);

		return JsonConverter.getAliases(response).size() + " aliases found!";
	}

	public String removeAlias(String applicationName, String alias) {

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		try {

			restUtils.sendDeleteCommand(authentificationUtils.finalHost + urlLoader.actionApplication
					+ application.getName() + "/alias/" + alias, authentificationUtils.getMap()).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);

		return "This alias has successful been deleted";
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String checkAndRejectIfError(String applicationName) {
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

		if (application == null && applicationName == null) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED
					+ "No application is currently selected by the following command line : use <application name>"
					+ ANSIConstants.ANSI_RESET;

		}
		String result = "";
		if (applicationName != null) {
			log.log(Level.INFO, applicationName);
			result = useApplication(applicationName);
			if (result.contains("This application does not exist on this account")) {
				return result;
			}
		}

		return null;
	}

	public String createEnvironmentVariable(String applicationName, String key, String value) {
		String response;

		if(application != null && applicationName == null)
			applicationName = application.getName();
		if(application == null && applicationName != null)
			useApplication(applicationName);

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put("keyEnv", key);
			parameters.put("valueEnv", value);

			restUtils.sendPostCommand(authentificationUtils.finalHost + urlLoader.actionApplication
							+ application.getName() + "/container/" + application.getServer().getName() + "/environmentVariables",
					authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);
		response = "An environment variable has been successfully added to " + application.getName();

		return response;
	}

	public String removeEnvironmentVariable(String applicationName, String key) {
		String response;

		if (authentificationUtils.getMap().isEmpty()) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + "You are not connected to CloudUnit host! Please use connect command"
					+ ANSIConstants.ANSI_RESET;
		}

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		try {
			response = restUtils.sendGetCommand(
					authentificationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/container/"
							+ application.getServer().getName() + "/environmentVariables",
					authentificationUtils.getMap()).get("body");

			ObjectMapper mapper = new ObjectMapper();
			List<EnvironmentVariable> environmentVariables = JsonConverter.getEnvironmentVariables(response);
			int id = -1;
			for(EnvironmentVariable var : environmentVariables)
				if(var.getKeyEnv().equals(key))
					id = var.getId();

			restUtils.sendDeleteCommand(authentificationUtils.finalHost + urlLoader.actionApplication
							+ application.getName() + "/container/" + application.getServer().getName() + "/environmentVariables/" + id,
					authentificationUtils.getMap()).get("body");
		} catch (Exception e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);

		return "This environment variable has successful been deleted";
	}

	public String updateEnvironmentVariable(String applicationName, String oldKey, String newKey, String value) {
		String response;

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		try {
			response = restUtils.sendGetCommand(
					authentificationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/container/"
							+ application.getServer().getName() + "/environmentVariables",
					authentificationUtils.getMap()).get("body");

			ObjectMapper mapper = new ObjectMapper();
			List<EnvironmentVariable> environmentVariables = JsonConverter.getEnvironmentVariables(response);
			int id = -1;
			Map<String, String> parameters = new HashMap<>();
			for(EnvironmentVariable var : environmentVariables)
				if(var.getKeyEnv().equals(oldKey)) {
					id = var.getId();
					parameters.put("keyEnv", newKey);
					parameters.put("valueEnv", value);
				}

			restUtils.sendPutCommand(authentificationUtils.finalHost + urlLoader.actionApplication
							+ application.getName() + "/container/" + application.getServer().getName() + "/environmentVariables/" + id,
					authentificationUtils.getMap(), parameters).get("body");
		} catch (Exception e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);

		return "This environment variable has successful been updated";
	}

	public String listAllEnvironmentVariables(String applicationName) {
		String response = null;

		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		try {
			response = restUtils.sendGetCommand(
					authentificationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/container/"
							+ application.getServer().getName() + "/environmentVariables",
					authentificationUtils.getMap()).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		MessageConverter.buildListEnvironmentVariables(JsonConverter.getEnvironmentVariables(response));

		statusCommand.setExitStatut(0);

		return JsonConverter.getEnvironmentVariables(response).size() + " variables found!";
	}

	public String listContainers(String applicationName) {
		String checkResponse = checkAndRejectIfError(applicationName);
		if (checkResponse != null) {
			return checkResponse;
		}

		List<String> containers = new ArrayList<>();
		containers.add(getApplication().getServer().getName());

		for (Module module : getApplication().getModules()) {
			containers.add(module.getName());
		}
		MessageConverter.buildListContainers(containers);

		statusCommand.setExitStatut(0);

		return containers.size() + " containers found!";
	}

	public String listCommands(String containerName) {
		String response;

		try {
			response = restUtils.sendGetCommand(
					authentificationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/container/"
							+ application.getServer().getName() + "/command",
					authentificationUtils.getMap()).get("body");


		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		MessageConverter.buildListCommands(JsonConverter.getCommands(response));

		statusCommand.setExitStatut(0);

		return JsonConverter.getCommands(response).size() + " commands found!";
	}

	public String execCommand(String name, String containerName, String arguments) {
		String response;

		if(containerName == null)
		{
			if(getApplication() == null) {
				statusCommand.setExitStatut(1);
				return ANSIConstants.ANSI_RED
						+ "No application is currently selected by the following command line : use <application name>"
						+ ANSIConstants.ANSI_RESET;
			}
			containerName = getApplication().getServer().getName();
		}

		try {
            Command command = new Command();
            command.setName(name);
            command.setArguments(Arrays.asList(arguments.split(",")));
            ObjectMapper objectMapper = new ObjectMapper();
            String entity = objectMapper.writeValueAsString(command);
			restUtils.sendPostCommand(
					authentificationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/container/"
							+ application.getServer().getName() + "/command/" + name + "/exec",
					authentificationUtils.getMap(), entity);

		} catch (ManagerResponseException | JsonProcessingException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		statusCommand.setExitStatut(0);

		return "The command " + name + " has been executed";
	}
}

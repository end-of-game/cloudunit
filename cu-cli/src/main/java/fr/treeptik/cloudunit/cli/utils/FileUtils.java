/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https:gnu.org/licenses/agpl.html
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.cli.shell.CloudUnitPromptProvider;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;

@Component
public class FileUtils {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthentificationUtils authentificationUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private ApplicationUtils applicationUtils;

	@Autowired
	private CloudUnitPromptProvider clPromptProvider;

	private String currentContainer;

	private String currentPath;

	public String openExplorer(String containerName) {

		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer != null) {
			log.log(Level.SEVERE,
					"You are already into a container file explorer. Please exit it with close-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}

		Application application = applicationUtils.getApplication();
		Server server = application.getServer();

		if (server.getName().equalsIgnoreCase(containerName)) {
			currentContainer = server.getContainerID();

		}

		for (Module module : application.getModules()) {
			if (module.getName().equalsIgnoreCase(containerName)) {
				currentContainer = module.getContainerID();
				break;
			}
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE,
					"This container name doesn't exist. Please choose one of following container name : ");
			displayAvailableContainerNames();
			statusCommand.setExitStatut(1);
			return null;
		}

		clPromptProvider.setPrompt("cloudunit>[" + containerName + "] ");
		currentPath = "/";

		return null;
	}

	public String closeExplorer() throws ManagerResponseException {

		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer != null) {
			currentContainer = null;
			currentPath = null;
			clPromptProvider.setPrompt("cloudunit> ");
		} else {
			log.log(Level.WARNING, "You are not in a container file explorer");
			return null;
		}
		return "File explorer closed!";
	}

	public String listFiles() throws ManagerResponseException {

		String json = null;
		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}

		String command = authentificationUtils.finalHost + "/file/container/" + currentContainer + "?path="+ currentPath;
		log.info(command);
		json = restUtils.sendGetCommand(command, authentificationUtils.getMap()).get("body");
		statusCommand.setExitStatut(0);

		MessageConverter.buildListFileUnit(JsonConverter.getFileUnits(json));

		return null;
	}

	public String checkPath(String path) throws ManagerResponseException {

		String json = null;

		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (!path.startsWith("/"))
			path = "/" + path;
		path = currentPath + path;
		String command = authentificationUtils.finalHost + "/file/container/" + currentContainer + "?path/" + path;
		json = restUtils.sendGetCommand(command, authentificationUtils.getMap()).get("body");
		if (json.equals("[]")) {
			return "This directory does not exist";
		}

		return "This directory exist";
	}

	public String enterDirectory(String directoryName) throws ManagerResponseException {

		String json = null;
		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}

		String command = authentificationUtils.finalHost + "/file/container/" + currentContainer + "?path=" + currentPath;
		json = restUtils.sendGetCommand(command, authentificationUtils.getMap()).get("body");

		List<FileUnit> fileUnits = JsonConverter.getFileUnits(json);
		currentPath = directoryName;
        System.out.println("currentPath : " + currentPath);

        listFiles();
		statusCommand.setExitStatut(0);
		return null;
	}

	public String enterPathDirectory(List<String> pathList) throws ManagerResponseException {
		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}

		for (String path : pathList) {
			if (path.equals("..")) {
				currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
				clPromptProvider.setPrompt(
						clPromptProvider.getPrompt().substring(0, clPromptProvider.getPrompt().lastIndexOf("/")) + " ");
			} else {
				clPromptProvider.setPrompt(clPromptProvider.getPrompt().trim() + "/" + path + " ");
			}
		}

		listFiles();
		statusCommand.setExitStatut(0);
		return null;
	}

	public String unzip(String fileName) {

		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}

		String command = authentificationUtils.finalHost + "/file/unzip/container/" + currentContainer + "/application/"
				+ applicationUtils.getApplication().getName() + "?path=" + currentPath + "/fileName/" + fileName;
		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getApplication().getName());
		try {
			restUtils.sendPutCommand(command, authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		applicationUtils.useApplication(applicationUtils.getApplication().getName());

		return null;
	}

	public String uploadFile(File path) {

		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}

		File file = path;

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.available();
			fileInputStream.close();
			FileSystemResource resource = new FileSystemResource(file);
			Map<String, Object> params = new HashMap<>();
			params.put("file", resource);
			params.putAll(authentificationUtils.getMap());
			restUtils.sendPostForUpload(authentificationUtils.finalHost + "/file/container/" + currentContainer
					+ "/application/" + applicationUtils.getApplication().getName() + "?path=" + currentPath, params);
			statusCommand.setExitStatut(0);

		} catch (IOException e) {

			log.log(Level.SEVERE, "File not found! Check the path file");
			statusCommand.setExitStatut(1);
		}

		return null;
	}

	public String downloadFile(String fileName, String destination) throws ManagerResponseException {

		if (authentificationUtils.getMap().isEmpty()) {
			log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
			statusCommand.setExitStatut(1);
			return null;
		}
		if (applicationUtils.getApplication() == null) {
			log.log(Level.SEVERE,
					"No application is currently selected by the followind command line : use <application name>");
			statusCommand.setExitStatut(1);
			return null;
		}

		if (currentContainer == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}
		/*
		 * check si c'est un fichier ou un directory (interdiction)
		 */
		boolean fileExists = false;

		String json = restUtils.sendGetCommand(
				authentificationUtils.finalHost + "/file/container/" + currentContainer + "?path=" + currentPath,
				authentificationUtils.getMap()).get("body");

		List<FileUnit> fileUnits = JsonConverter.getFileUnits(json);
		for (FileUnit fileUnit : fileUnits) {
			if (fileUnit.getName().equalsIgnoreCase(fileName)) {
				if (fileUnit.isDir()) {
					log.log(Level.SEVERE, "This file should not be a directory");
					return null;
				}
				fileExists = true;
			}
		}
		if (!fileExists) {
			log.log(Level.SEVERE, "This file does not exist");
			return null;
		}
		String destFileName = System.getProperty("user.home") + "/" + fileName;

		if (destination != null) {
			destFileName = destination + "/" + fileName;
		}

		Map<String, Object> params = new HashMap<>();
		params.putAll(authentificationUtils.getMap());
		restUtils.sendGetFileCommand(authentificationUtils.finalHost + "/file/container/" + currentContainer
				+ "/application/" + applicationUtils.getApplication().getName() + "?path=" + currentPath + "/fileName/"
				+ fileName, destFileName, params);
		statusCommand.setExitStatut(0);
		return "File correctly send in this default location : " + destFileName;
	}

	public boolean isInFileExplorer() {
		return currentContainer != null;
	}

	public void displayAvailableContainerNames() {
		StringBuilder builder = new StringBuilder();
		Server server = applicationUtils.getApplication().getServer();

		builder.append("\t" + server.getName() + "\t");

		for (Module module : applicationUtils.getApplication().getModules()) {
			builder.append("\t" + module.getName() + "\t");
		}
		log.log(Level.INFO, builder.toString());
	}

}

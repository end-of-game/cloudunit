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

import fr.treeptik.cloudunit.cli.Guard;
import fr.treeptik.cloudunit.cli.Messages;
import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;

@Component
public class FileUtils {
    private static final String NOT_IN_EXPLORER = Messages.getString("file.NOT_IN_EXPLORER");
    private static final String IN_EXPLORER = Messages.getString("file.IN_EXPLORER");

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthenticationUtils authentificationUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private ApplicationUtils applicationUtils;

	private String currentContainerName;

	private String currentPath;
	
	public String getCurrentContainerName() {
        return currentContainerName;
    }

    public String createDirectory(String path) {
        if (checkSecurity()) return null;
        String url = authentificationUtils.finalHost + "/file/container/" + currentContainerName
                + "/application/" + applicationUtils.getCurrentApplication().getName();
        try {
            Map<String, Object> results = restUtils.sendPostCommand(url + "?path=" + path, authentificationUtils.getMap(), "");
            statusCommand.setExitStatut(0);
        } catch (Exception e) {
            statusCommand.setExitStatut(1);
            return "error";
        }
        return "created";
    }

    private boolean checkSecurity() {
        if (authentificationUtils.getMap().isEmpty()) {
            log.log(Level.SEVERE, "You are not connected to CloudUnit host! Please use connect command");
            statusCommand.setExitStatut(1);
            return true;
        }
        if (applicationUtils.getCurrentApplication() == null) {
            log.log(Level.SEVERE,
                    "No application is currently selected by the followind command line : use <application name>");
            statusCommand.setExitStatut(1);
            return true;
        }
        return false;
    }


    public String openExplorer(String containerName) {
        if (checkSecurity()) return null;
		Application application = applicationUtils.getCurrentApplication();
		Server server = application.getServer();
		if (server.getName().equalsIgnoreCase(containerName)) {
			currentContainerName = server.getContainerID();
		}
		for (Module module : application.getModules()) {
			if (module.getName().equalsIgnoreCase(containerName)) {
				currentContainerName = module.getContainerID();
				break;
			}
		}
		if (currentContainerName == null) {
			log.log(Level.SEVERE,
					"This container name doesn't exist. Please choose one of following container name : ");
			displayAvailableContainerNames();
			statusCommand.setExitStatut(1);
			return null;
		}

		currentContainerName = containerName;
		currentPath = "/";
		return "";
	}

    /**
     * Close explorer
     *
     * @return
     * @throws ManagerResponseException
     */
	public String closeExplorer() throws ManagerResponseException {
        if (checkSecurity()) return null;
        if (currentContainerName != null) {
			currentContainerName = null;
			currentPath = null;
		} else {
			log.log(Level.WARNING, "You are not in a container file explorer");
			return null;
		}
		return "File explorer closed!";
	}

    /**
     * List the files for current directory
     *
     * @return
     * @throws ManagerResponseException
     */
	public String listFiles() throws ManagerResponseException {
        if (checkSecurity()) return null;
        if (currentContainerName == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}
		String command = authentificationUtils.finalHost + "/file/container/" + currentContainerName + "?path="+ currentPath;
		log.info(command);
		String json = restUtils.sendGetCommand(command, authentificationUtils.getMap()).get("body");
		statusCommand.setExitStatut(0);
		String result = MessageConverter.buildListFileUnit(JsonConverter.getFileUnits(json));
		return result;
	}

    /**
     * Change directory
     *
     * @param directoryName
     * @return
     * @throws ManagerResponseException
     */
	public String changeDirectory(String directoryName) throws ManagerResponseException {
        if (checkSecurity()) return null;
        if (currentContainerName == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}
		String command = authentificationUtils.finalHost + "/file/container/" + currentContainerName + "?path=" + currentPath;
		String json = restUtils.sendGetCommand(command, authentificationUtils.getMap()).get("body");
		List<FileUnit> fileUnits = JsonConverter.getFileUnits(json);
		currentPath = directoryName;
		statusCommand.setExitStatut(0);
		return "current directory is now : " + directoryName;
	}

    /**
     * Unzip a file
     *
     * @param fileName
     * @return
     */
	public String unzip(String fileName) {
        if (checkSecurity()) return null;
		if (currentContainerName == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the open-explorer command");
			statusCommand.setExitStatut(1);
			return null;
		}
        String currentPath = fileName.substring(0, fileName.lastIndexOf("/"));
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
		String command = authentificationUtils.finalHost + "/file/unzip/container/" + currentContainerName + "/application/"
				+ applicationUtils.getCurrentApplication().getName() + "?path=" + currentPath + "&fileName=" + fileName;
		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getCurrentApplication().getName());
		try {
			restUtils.sendPutCommand(command, authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}
		applicationUtils.useApplication(applicationUtils.getCurrentApplication().getName());
		return null;
	}

	public String uploadFile(File path) {
        if (checkSecurity()) return null;
        if (currentContainerName == null) {
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
			restUtils.sendPostForUpload(authentificationUtils.finalHost + "/file/container/" + currentContainerName
					+ "/application/" + applicationUtils.getCurrentApplication().getName() + "?path=" + currentPath, params);
			statusCommand.setExitStatut(0);
		} catch (IOException e) {
			log.log(Level.SEVERE, "File not found! Check the path file");
			statusCommand.setExitStatut(1);
		}
		return null;
	}

    /**
     * Download a file
     *
     * @param fileName
     * @param destination
     * @return
     * @throws ManagerResponseException
     */
	public String downloadFile(String fileName, String destination) throws ManagerResponseException {

        if (checkSecurity()) return null;
        if (currentContainerName == null) {
			log.log(Level.SEVERE, "You're not in a container file explorer. Please use the 'open-explorer' command");
			statusCommand.setExitStatut(1);
			return null;
		}
		boolean fileExists = false;
        String json = restUtils.sendGetCommand(
				authentificationUtils.finalHost + "/file/container/" + currentContainerName + "?path=" + currentPath,
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
		restUtils.sendGetFileCommand(authentificationUtils.finalHost + "/file/container/" + currentContainerName
				+ "/application/" + applicationUtils.getCurrentApplication().getName() + "?path=" + currentPath + "/fileName/"
				+ fileName, destFileName, params);
		statusCommand.setExitStatut(0);
		return "File correctly send in this default location : " + destFileName;
	}

	public boolean isInFileExplorer() {
		return currentContainerName != null;
	}
	
	public void checkInFileExplorer() {
	    Guard.guardTrue(isInFileExplorer(), NOT_IN_EXPLORER);
	}
	
	public void checkNotInFileExplorer() {
	    Guard.guardTrue(!isInFileExplorer(), IN_EXPLORER);
	}

	public void displayAvailableContainerNames() {
		StringBuilder builder = new StringBuilder();
		Server server = applicationUtils.getCurrentApplication().getServer();

		builder.append("\t" + server.getName() + "\t");

		for (Module module : applicationUtils.getCurrentApplication().getModules()) {
			builder.append("\t" + module.getName() + "\t");
		}
		log.log(Level.INFO, builder.toString());
	}

}

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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
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
    private static final String NO_SUCH_CONTAINER = "No such container \"{0}\". Available containers are\n{1}";
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

	private String currentContainerId;

	private String currentPath;
	
	public String getCurrentContainerName() {
        return currentContainerId;
    }

    public boolean isInFileExplorer() {
        return currentContainerId != null;
    }
    
    public void checkInFileExplorer() {
        Guard.guardTrue(isInFileExplorer(), NOT_IN_EXPLORER);
    }
    
    public void checkNotInFileExplorer() {
        Guard.guardTrue(!isInFileExplorer(), IN_EXPLORER);
    }
    
    public void checkConnectedAndInFileExplorer() {
        authentificationUtils.checkConnected();
        applicationUtils.checkApplicationSelected();
        checkInFileExplorer();
    }

    public String createDirectory(String path) {
        checkConnectedAndInFileExplorer();
        
        String url = authentificationUtils.finalHost + "/file/container/" + currentContainerId
                + "/application/" + applicationUtils.getCurrentApplication().getName();
        try {
            restUtils.sendPostCommand(url + "?path=" + path, authentificationUtils.getMap(), "");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't create directory", e);
        }
        return MessageFormat.format("Directory \"{0}\" created", path);
    }

    public String openExplorer(String containerName) {
        applicationUtils.checkConnectedAndApplicationSelected();
        
		Application application = applicationUtils.getCurrentApplication();
		
		Server server = application.getServer();
		if (server.getName().equalsIgnoreCase(containerName)) {
			currentContainerId = server.getContainerID();
		} else {
		    Optional<Module> module = application.getModules().stream()
		        .filter(m -> m.getName().equalsIgnoreCase(containerName))
		        .findAny();
		    
		    if (!module.isPresent()) {
		        throw new CloudUnitCliException(MessageFormat.format(NO_SUCH_CONTAINER,
		                containerName,
		                getAvailableContainerNames()));
		    }
		    
		    currentContainerId = module.get().getContainerID();
		}

		currentPath = "/";
		return "Explorer opened";
	}

    /**
     * Close explorer
     *
     * @return
     * @throws ManagerResponseException
     */
	public String closeExplorer() throws ManagerResponseException {
        checkConnectedAndInFileExplorer();
	        
		currentContainerId = null;
		currentPath = null;
		
		return "Explorer closed";
	}

    /**
     * List the files for current directory
     *
     * @return
     * @throws ManagerResponseException
     */
	public String listFiles() throws ManagerResponseException {
        checkConnectedAndInFileExplorer();
	    
		String url = authentificationUtils.finalHost + "/file/container/" + currentContainerId + "?path="+ currentPath;
		String json = restUtils.sendGetCommand(url, authentificationUtils.getMap()).get("body");

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
        checkConnectedAndInFileExplorer();
	    
		String url = authentificationUtils.finalHost + "/file/container/" + currentContainerId + "?path=" + currentPath;
		String json = restUtils.sendGetCommand(url, authentificationUtils.getMap()).get("body");
		JsonConverter.getFileUnits(json);
		currentPath = directoryName;
		statusCommand.setExitStatut(0);
		return "Current directory is " + directoryName;
	}

    /**
     * Unzip a file
     *
     * @param fileName
     * @return
     */
	public String unzip(String fileName) {
        checkConnectedAndInFileExplorer();
	    
        String currentPath = fileName.substring(0, fileName.lastIndexOf("/"));
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
		String command = authentificationUtils.finalHost + "/file/unzip/container/" + currentContainerId + "/application/"
				+ applicationUtils.getCurrentApplication().getName() + "?path=" + currentPath + "&fileName=" + fileName;
		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getCurrentApplication().getName());
		try {
			restUtils.sendPutCommand(command, authentificationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't unzip file", e);
		}
		return "File unzipped";
	}

	public String uploadFile(File path) {
        checkConnectedAndInFileExplorer();
	    
		File file = path;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			fileInputStream.available();
			fileInputStream.close();
			FileSystemResource resource = new FileSystemResource(file);
			Map<String, Object> params = new HashMap<>();
			params.put("file", resource);
			params.putAll(authentificationUtils.getMap());
			restUtils.sendPostForUpload(authentificationUtils.finalHost + "/file/container/" + currentContainerId
					+ "/application/" + applicationUtils.getCurrentApplication().getName() + "?path=" + currentPath, params);
		} catch (IOException e) {
			log.log(Level.SEVERE, "File not found! Check the path file");
			statusCommand.setExitStatut(1);
		}
		return "File uploaded";
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
	    checkConnectedAndInFileExplorer();
        
        String json = restUtils.sendGetCommand(
				authentificationUtils.finalHost + "/file/container/" + currentContainerId + "?path=" + currentPath,
				authentificationUtils.getMap()).get("body");

		List<FileUnit> fileUnits = JsonConverter.getFileUnits(json);
		
		FileUnit fileUnit = fileUnits.stream()
		    .filter(f -> f.getName().equalsIgnoreCase(fileName))
		    .findAny()
		    .orElseThrow(() -> new CloudUnitCliException(MessageFormat.format("No such file \"{0}\"", fileName)));
		
		if (fileUnit.isDir()) {
		    throw new CloudUnitCliException("Cannot download a directory");
		}

		String destFileName = System.getProperty("user.home") + "/" + fileName;

		if (destination != null) {
			destFileName = destination + "/" + fileName;
		}

		Map<String, Object> params = new HashMap<>();
		params.putAll(authentificationUtils.getMap());
		restUtils.sendGetFileCommand(authentificationUtils.finalHost + "/file/container/" + currentContainerId
				+ "/application/" + applicationUtils.getCurrentApplication().getName() + "?path=" + currentPath + "/fileName/"
				+ fileName, destFileName, params);
		
		return MessageFormat.format("File downloaded to {0}", destFileName);
	}

	private String getAvailableContainerNames() {
		StringBuilder builder = new StringBuilder();
		Server server = applicationUtils.getCurrentApplication().getServer();

		builder.append("\t");
		builder.append(server.getName());

		for (Module module : applicationUtils.getCurrentApplication().getModules()) {
		    builder.append("\t");
			builder.append("\t" + module.getName());
		}
		return builder.toString();
	}

}

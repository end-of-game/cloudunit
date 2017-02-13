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
package fr.treeptik.cloudunit.cli.commands;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.Guard;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.utils.FileUtils;
import fr.treeptik.cloudunit.dto.FileUnit;

@Component
public class FileCommands implements CommandMarker {
	@Autowired
	private FileUtils fileUtils;
	
	@Autowired
	private CliFormatter formatter;

	@CliCommand(value = "unzip", help = "Unzip the file")
	public String unzipFile(@CliOption(key = "file") String fileName) {
		fileUtils.unzip(fileName);
		return formatter.unlessQuiet("File unzipped");
	}

	@CliCommand(value = "open-explorer", help = "Open the file explorer of the container")
	public String openFileExplorer(@CliOption(key = "containerName") String containerName) {
		fileUtils.openExplorer(containerName);
		return formatter.unlessQuiet("File explorer open");
	}

	@CliCommand(value = "close-explorer", help = "Close the current file explorer")
	public String closeFileExplorer() throws ManagerResponseException {
		fileUtils.closeExplorer();
		return formatter.unlessQuiet("File explorer closed");
	}

	@CliCommand(value = "list-files", help = "Show files into the current path")
	public String listFilesByContainerAndPath() throws ManagerResponseException {
		List<FileUnit> files = fileUtils.listFiles();
		List<String> fileNames = files.stream()
		        .map(f -> f.getName())
		        .collect(Collectors.toList());
        return formatter.list(fileNames);
	}

	@CliCommand(value = {"create-directory", "mkdir"}, help = "Create directory")
	public String createDirectory(@CliOption(key = { "", "path" }) String path) throws ManagerResponseException {
		fileUtils.createDirectory(path);
		
		return formatter.unlessQuiet(MessageFormat.format("Directory \"{0}\" created", path));
	}

	@CliCommand(value = {"change-directory", "cd"}, help = "Enter into a directory")
	public String enterDirectory(@CliOption(key = { "" }) String directoryName) throws ManagerResponseException {
	    Guard.guardTrue(StringUtils.isNotBlank(directoryName), "You must specify a directory");
		fileUtils.changeDirectory(directoryName);
		return null;
	}

	@CliCommand(value = "upload-file", help = "Upload a file into the current directory")
	public String upload(@CliOption(key = { "path" }, mandatory = true, help = "Path of the file") File path)
			throws URISyntaxException, MalformedURLException, ManagerResponseException {
	    Guard.guardTrue(path.exists(), "File does not exist");
	    Guard.guardTrue(path.isFile(), "Path cannot reference a directory");
		
		fileUtils.uploadFile(path);
		
		return formatter.unlessQuiet("File uploaded");
	}

	@CliCommand(value = "download-file", help = "Download a file into the current directory")
	public String upload(
	        @CliOption(key = { "fileName" }, mandatory = true, help = "Path of the file") String fileName,
			@CliOption(key = { "destination" }, mandatory = false, help = "Destination") String destination)
			throws URISyntaxException, MalformedURLException, ManagerResponseException {
		String actualDestination = fileUtils.downloadFile(fileName, destination);
		
		return formatter.unlessQuiet(MessageFormat.format("File downloaded to {0}", actualDestination));
	}

}

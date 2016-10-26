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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.utils.FileUtils;

@Component
public class FileCommands implements CommandMarker {

	@Autowired
	private FileUtils fileUtils;

	@CliCommand(value = "unzip", help = "Unzip the file")
	public String unzipFile(@CliOption(key = "file") String fileName) {
		return fileUtils.unzip(fileName);
	}

	@CliCommand(value = "open-explorer", help = "Open the file explorer of the container")
	public String openFileExplorer(@CliOption(key = "containerName") String containerName) {
		return fileUtils.openExplorer(containerName);
	}

	@CliCommand(value = "close-explorer", help = "Close the current file explorer")
	public String closeFileExplorer() throws ManagerResponseException {
		return fileUtils.closeExplorer();
	}

	@CliCommand(value = "list-files", help = "Show files into the current path")
	public String listFilesByContainerAndPath() throws ManagerResponseException {
		return fileUtils.listFiles();
	}

	@CliCommand(value = {"create-directory", "mkdir"}, help = "Create directory")
	public String createDirectory(@CliOption(key = { "", "path" }) String path) throws ManagerResponseException {
		return fileUtils.createDirectory(path);
	}

	@CliCommand(value = {"change-directory", "cd"}, help = "Enter into a directory")
	public String enterDirectory(@CliOption(key = { "", "directoryName" }) String directoryName,
			@CliOption(key = {
					"parent" }, mandatory = false, help = "Return at the parent directory", specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") Boolean parent)
			throws ManagerResponseException {

        if (directoryName == null || directoryName.trim().length() == 0) { return "Need an directory as argument."; }
		return fileUtils.changeDirectory(directoryName);
	}

	@CliCommand(value = "upload-file", help = "Upload a file into the current directory")
	public String upload(@CliOption(key = { "path" }, mandatory = true, help = "Path of the file") File path)
			throws URISyntaxException, MalformedURLException, ManagerResponseException {

		if (path.exists() && path.isFile()) {
			return fileUtils.uploadFile(path);
		}
		return "Check your syntax and option chosen and it's the right path";
	}

	@CliCommand(value = "download-file", help = "Download a file into the current directory")
	public String upload(@CliOption(key = { "fileName" }, mandatory = true, help = "Path of the file") String fileName,
			@CliOption(key = { "destination" }, mandatory = false, help = "Path of the file") String destination)
			throws URISyntaxException, MalformedURLException, ManagerResponseException {

		return fileUtils.downloadFile(fileName, destination);
	}

}

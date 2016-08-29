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

import fr.treeptik.cloudunit.cli.utils.ApplicationUtils;

@Component
public class ApplicationCommands implements CommandMarker {

	@Autowired
	private ApplicationUtils applicationUtils;

	@CliCommand(value = "informations", help = "Display informations about the current application")
	public String getApplication() {
		return applicationUtils.getInformations();
	}

	@CliCommand(value = "use", help = "Take control of an application")
	public String useApp(@CliOption(key = { "",
			"name" }, mandatory = true, help = "Application name. Use list-apps to show all available apps on this account") String name) {
		return applicationUtils.useApplication(name);
	}

	@CliCommand(value = "create-app", help = "Take control of an application")
	public String createApp(@CliOption(key = { "name" }, mandatory = true, help = "Application name") String name,
			@CliOption(key = {
					"type" }, mandatory = true, help = "Server type : \n Available servers are the following : \n - JBoss 8 : -type jboss-8 \n - Apache Tomcat 6 : -type tomcat-6 \n - Apache Tomcat 7 : -type tomcat-7 \n - Apache Tomcat 8 : -type tomcat-8") String serverName) {
		return applicationUtils.createApp(name, serverName);
	}

	@CliCommand(value = "rm-app", help = "Remove an application")
	public String rmApp(
			@CliOption(key = {
					"name" }, mandatory = false, help = "Application name to remove ") String applicationName,
			@CliOption(key = {
					"scriptUsage" }, mandatory = false, help = "Non-interactive mode", specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") Boolean scriptUsage) {
		return applicationUtils.rmApp(applicationName, scriptUsage);
	}

	@CliCommand(value = "start", help = "Start the current application and  all its services")
	public String startApp(@CliOption(key = {
			"name" }, mandatory = false, help = "Application name to start ") String applicationName) {
		return applicationUtils.startApp(applicationName);
	}

	@CliCommand(value = "stop", help = "Stop the current application and all its services")
	public String stopApp(@CliOption(key = {
			"name" }, mandatory = false, help = "Application name to stop ") String applicationName) {
		return applicationUtils.stopApp(applicationName);
	}

	@CliCommand(value = "list-apps", help = "List all applications")
	public String list() {
		return applicationUtils.listAll();
	}

	@CliCommand(value = "deploy", help = "Deploy an archive ear/war on the app servers")
	public String deploy(@CliOption(key = { "path" }, mandatory = true, help = "Path of the archive file") File path,
			@CliOption(key = {
					"openBrowser" }, mandatory = false, help = "Open a browser to location", unspecifiedDefaultValue = "true") boolean openBrowser)
			throws URISyntaxException, MalformedURLException {

		if (path.exists() == true && path.isFile() == true) {
			return applicationUtils.deployFromAWar(path, openBrowser);
		}
		return "Check your syntax and option chosen and it's the right path";
	}

	@CliCommand(value = "list-aliases", help = "Display all application aliases")
	public String listAlias(
			@CliOption(key = { "", "name" }, mandatory = false, help = "Application name") String applicationName) {
		return applicationUtils.listAllAliases(applicationName);
	}

	@CliCommand(value = "add-alias", help = "Add a new alias")
	public String addAlias(
			@CliOption(key = { "" }, mandatory = false, help = "Application name") String applicationName,
			@CliOption(key = { "alias" }, mandatory = true, help = "Alias to access to your apps") String alias) {
		return applicationUtils.addNewAlias(applicationName, alias);
	}

	@CliCommand(value = "rm-alias", help = "Remove an existing alias")
	public String rmAlias(
			@CliOption(key = { "", "name" }, mandatory = false, help = "Application name") String applicationName,
			@CliOption(key = { "alias" }, mandatory = true, help = "Alias to access to your apps") String alias) {
		return applicationUtils.removeAlias(applicationName, alias);
	}

	@CliCommand(value = "create-var-env", help = "Create a new environment variable")
	public String createEnvironmentVariable(
			@CliOption(key = {"name"}, mandatory = false, help = "Application name to remove ") String applicationName,
			@CliOption(key = {"", "key"}, mandatory = true, help = "Key to the environment variable") String key,
			@CliOption(key = {"", "value"}, mandatory = true, help = "Value to the environment variable") String value) {
		return applicationUtils.createEnvironmentVariable(applicationName, key, value);
	}

    @CliCommand(value = "list-var-env", help = "List all environment variables")
    public String listEnvironmentVariables(
            @CliOption(key = {"name"}, mandatory = false, help = "Application name to remove ") String applicationName) {
        return applicationUtils.listAllEnvironmentVariables(applicationName);
    }
}

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.tables.ApplicationTableColumn;
import fr.treeptik.cloudunit.cli.tables.EnvironmentVariableTableColumn;
import fr.treeptik.cloudunit.cli.utils.ApplicationUtils;
import fr.treeptik.cloudunit.cli.utils.DateUtils;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.EnvironmentVariable;

@Component
public class ApplicationCommands implements CommandMarker {
    private static final String HELP_APPLICATION_NAME =
	        "Application name. Use list-apps to show all available apps on this account";

    private static final String HELP_SERVER_TYPE = "Server type: \n"
	        + " Available servers are the following:\n"
	        + " - Wildfly 8: --type wildfly-8\n"
	        + " - Apache Tomcat 6: --type tomcat-6\n"
	        + " - Apache Tomcat 7: --type tomcat-7\n"
	        + " - Apache Tomcat 8: --type tomcat-8";
    
    private static final String ENV_VAR_ADDED = "Environment variable \"{0}\" has been added to application \"{1}\"";
    private static final String ENV_VAR_REMOVED = "Environment variable \"{0}\" has been removed from application \"{1}\"";
    private static final String APPLICATION_INFO = "Name: {0}\nOwner: {1} {2}\nCreated: {3}\nServer: {4}\nStatus: {5}";
    private static final String APPLICATION_CREATED = "Application \"{0}\" has been created";
    private static final String APPLICATION_REMOVED = "Application \"{0}\" has been removed";
    private static final String APPLICATION_STARTED = "Application \"{0}\" has been started";
    private static final String APPLICATION_STOPPED = "Application \"{0}\" has been stopped";
    private static final String APPLICATION_USING = "Using application \"{0}\"";
    private static final String APPLICATION_DEPLOYED = "Application \"{0}\" deployed. Access at {1}";
	
    @Autowired
	private ApplicationUtils applicationUtils;
    
    @Autowired
    private CliFormatter formatter;

	@CliCommand(value = "informations", help = "Display informations about the current application")
	public String getApplication() {
	    Application application = applicationUtils.getCurrentApplication();
	    if (application == null) {
	        return "No application selected";
	    }
		return MessageFormat.format(APPLICATION_INFO, application.getName(),
                application.getUser().getLastName(),
                application.getUser().getFirstName(),
                DateUtils.formatDate(application.getDate()),
                application.getServer().getImage().getName().toUpperCase(),
                application.getStatus().toString());
	}

	@CliCommand(value = "use", help = "Take control of an application")
	public String useApp(
	        @CliOption(key = {"","name"}, mandatory = true, help = HELP_APPLICATION_NAME) String name) {
		Application application = applicationUtils.useApplication(name);
		String message = MessageFormat.format(APPLICATION_USING,
		        application.getName());
        return formatter.unlessQuiet(message);
	}

	@CliCommand(value = "create-app", help = "Create an application")
	public String createApp(
	        @CliOption(key = "name", mandatory = true, help = "Application name") String name,
			@CliOption(key = "type", mandatory = true, help = HELP_SERVER_TYPE) String serverName) {
	    Application application = applicationUtils.createApp(name, serverName);
	    String message = MessageFormat.format(APPLICATION_CREATED, application.getName());
		return formatter.unlessQuiet(message);
	}

	@CliCommand(value = "rm-app", help = "Remove an application")
	public String rmApp(
			@CliOption(key = "name", mandatory = false, help = HELP_APPLICATION_NAME) String applicationName,
			@CliOption(key = "errorIfNotExists", mandatory = false, help = "Throw an error if not exists",
			    specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") Boolean errorIfNotExists,
			@CliOption(key = "scriptUsage", mandatory = false, help = "Non-interactive mode",
			    specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") Boolean scriptUsage) {
	    boolean removed = applicationUtils.rmApp(applicationName, errorIfNotExists, scriptUsage ? null : new CliPrompter());
	    
	    if (removed) {
	        return formatter.unlessQuiet(MessageFormat.format(APPLICATION_REMOVED, applicationName));
	    } else {
	        return null;
	    }
	}

	@CliCommand(value = "start", help = "Start the current application and all its services")
	public String startApp(
	        @CliOption(key = "name", mandatory = false, help = HELP_APPLICATION_NAME) String applicationName) {
		applicationUtils.startApp(applicationName);
		
		return formatter.unlessQuiet(MessageFormat.format(APPLICATION_STARTED, applicationName));
	}

	@CliCommand(value = "stop", help = "Stop the current application and all its services")
	public String stopApp(
	        @CliOption(key = "name", mandatory = false, help = HELP_APPLICATION_NAME) String applicationName) {
		applicationUtils.stopApp(applicationName);
		
		return formatter.unlessQuiet(MessageFormat.format(APPLICATION_STOPPED, applicationName));
	}

	@CliCommand(value = "list-apps", help = "List all applications")
	public String list() {
		List<Application> applications = applicationUtils.listAllApps();
        return formatter.table(ApplicationTableColumn.values(), applications);
	}

	@CliCommand(value = "deploy", help = "Deploy an archive ear/war on the app servers")
	public String deploy(
	        @CliOption(key = "path", mandatory = true, help = "Path of the archive file") File path,
			@CliOption(key = "openBrowser", mandatory = false, help = "Open a browser to location",
			    unspecifiedDefaultValue = "true") boolean openBrowser)
			throws URISyntaxException, MalformedURLException {
	    applicationUtils.deployFromAWar(path, openBrowser);
	    
	    Application application = applicationUtils.getCurrentApplication();	    
	    String message = MessageFormat.format(APPLICATION_DEPLOYED,
	            application.getName(),
	            application.getLocation());
        return formatter.unlessQuiet(message);
	}

	@CliCommand(value = "create-env-var", help = "Create a new environment variable")
	public String createEnvironmentVariable(
			@CliOption(key = {"name"}, mandatory = false, help = HELP_APPLICATION_NAME) String applicationName,
			@CliOption(key = {"", "key"}, mandatory = true, help = "Key to the environment variable") String key,
			@CliOption(key = {"", "value"}, mandatory = true, help = "Value to the environment variable") String value) {
		applicationUtils.createEnvironmentVariable(applicationName, key, value);
		
		Application application = applicationUtils.getSpecificOrCurrentApplication(applicationName);
		String message = MessageFormat.format(ENV_VAR_ADDED, key, application.getName());
		return formatter.unlessQuiet(message);
	}

    @CliCommand(value = "rm-env-var", help = "Remove an environment variable")
    public String removeEnvironmentVariable(
            @CliOption(key = {"name"}, mandatory = false, help = HELP_APPLICATION_NAME) String applicationName,
            @CliOption(key = {"", "key"}, mandatory = true, help = "Key to the environment variable") String key) {
        applicationUtils.removeEnvironmentVariable(applicationName, key);
        
        Application application = applicationUtils.getSpecificOrCurrentApplication(applicationName);
        String message = MessageFormat.format(ENV_VAR_REMOVED, key, application.getName());
        return formatter.unlessQuiet(message);
    }

	@CliCommand(value = "set-env-var", help = "Set an existing environment variable")
	public String updateEnvironmentVariable(
			@CliOption(key = {"name"}, mandatory = false, help = HELP_APPLICATION_NAME) String applicationName,
			@CliOption(key = {"", "old-key"}, mandatory = true, help = "Old key to the environment variable") String oldKey,
			@CliOption(key = {"", "new-key"}, mandatory = true, help = "New key to the environment variable") String newKey,
			@CliOption(key = {"", "value"}, mandatory = true, help = "New value to the environment variable") String value) {
		applicationUtils.updateEnvironmentVariable(applicationName, oldKey, newKey, value);

		return null;
	}

    @CliCommand(value = "list-env-var", help = "List all environment variables")
    public String listEnvironmentVariables(
            @CliOption(key = "name", mandatory = false, help = HELP_APPLICATION_NAME)
            String applicationName,
            @CliOption(key = "export", mandatory = false, help = "Generate export script",
                    unspecifiedDefaultValue = "false", specifiedDefaultValue = "true")
            boolean export) {
        List<EnvironmentVariable> variables = applicationUtils.listAllEnvironmentVariables(applicationName);
        if (export) {
            return variablesExportScript(variables);
        } else {
            return formatter.table(EnvironmentVariableTableColumn.values(), variables);
        }
    }
    
    private String variablesExportScript(List<EnvironmentVariable> variables) {
        return variables.stream()
            .map(v -> String.format("export %s=\"%s\"", v.getKeyEnv(), v.getValueEnv()))
            .collect(Collectors.joining("\n"));
    }

    @CliCommand(value = "list-containers", help = "List all containers")
	public String listContainers(
			@CliOption(key = "name", mandatory = false, help = HELP_APPLICATION_NAME) String applicationName) {
    	List<String> containers = applicationUtils.listContainers(applicationName);
        return formatter.list(containers);
	}
}

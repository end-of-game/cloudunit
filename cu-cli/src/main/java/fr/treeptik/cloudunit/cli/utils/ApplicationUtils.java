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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
import fr.treeptik.cloudunit.cli.Guard;
import fr.treeptik.cloudunit.cli.Messages;
import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.Module;

@Component
public class ApplicationUtils {
    private static final String ENV_VAR_REMOVED = "Environment variable \"{0}\" has been removed from application \"{1}\"";
    private static final String NO_SUCH_ENV_VAR = "No such environment variable \"{0}\"";
    private static final String APPLICATION_CREATED = "Application \"{0}\" has been created";
    private static final String APPLICATION_REMOVED = "Application \"{0}\" has been removed";
    private static final String APPLICATION_STARTED = "Application \"{0}\" has been started";
    private static final String APPLICATION_STOPPED = "Application \"{0}\" has been stopped";
    private static final String NO_APPLICATION = Messages.getString("application.NO_APPLICATION");
    private static final String NO_SUCH_APPLICATION = Messages.getString("application.NO_SUCH_APPLICATION");

    @InjectLogger
    private Logger log;

    @Autowired
    private UrlLoader urlLoader;

    @Autowired
    private AuthenticationUtils authenticationUtils;

    @Autowired
    private ShellStatusCommand statusCommand;

    @Autowired
    private RestUtils restUtils;

    @Autowired
    private CheckUtils checkUtils;

    @Autowired
    private FileUtils fileUtils;

    private Application application;

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public boolean isApplicationSelected() {
        return application != null;
    }
    
    public void checkApplicationSelected() {
        Guard.guardTrue(isApplicationSelected(), NO_APPLICATION);
    }

    private void checkApplication(String applicationName) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();
        
        if (StringUtils.isEmpty(applicationName)) {
            checkApplicationSelected();
        } else {
            checkApplicationExists(applicationName);
        }
    }

    public boolean applicationExists(String applicationName) {
        // TODO use a proper query
        try {
            useApplication(applicationName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void checkApplicationExists(String applicationName) {
        Guard.guardTrue(applicationExists(applicationName), NO_SUCH_APPLICATION, applicationName);
    }

    public String getInformations() {
        checkApplication(null);

        useApplication(application.getName());
        String dockerManagerIP = application.getManagerIp();
        statusCommand.setExitStatut(0);

        MessageConverter.buildApplicationMessage(application, dockerManagerIP);
        return "Terminated";
    }

    public String useApplication(String applicationName) {
        String json = null;

        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();

        try {
            String url = authenticationUtils.finalHost + urlLoader.actionApplication + applicationName;
            json = restUtils.sendGetCommand(url, authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }
        statusCommand.setExitStatut(0);

        if (json != null && json.trim().length() > 0) {
            setApplication(JsonConverter.getApplication(json));
            return "Current application : " + getApplication().getName();
        } else {
            throw new CloudUnitCliException(MessageFormat.format(NO_SUCH_APPLICATION, applicationName));
        }
    }
    
    public String createApp(String applicationName, String serverName) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();

        try {
            if (checkUtils.checkImageNoExist(serverName)) {
                statusCommand.setExitStatut(1);
                return ANSIConstants.ANSI_RED + "This server image does not exist" + ANSIConstants.ANSI_RESET;
            }
            Map<String, String> parameters = new HashMap<>();
            parameters.put("applicationName", applicationName);
            parameters.put("serverName", serverName);

            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication,
                    authenticationUtils.getMap(), parameters).get("body");

            statusCommand.setExitStatut(0);
            
            useApplication(applicationName);

            return MessageFormat.format(APPLICATION_CREATED, application.getName());

        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }
    }

    public String rmApp(String applicationName, boolean errorIfNotExists, Prompter prompter) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();
        
        if (StringUtils.isEmpty(applicationName)) {
            checkApplicationSelected();
        } else if (errorIfNotExists) {
            checkApplicationExists(applicationName);
        } else if (!applicationExists(applicationName)) {
            return "";
        }
        
        if (prompter != null) {
            boolean confirmed = prompter.promptConfirmation(MessageFormat.format("Remove application \"{0}\"?",
                    application.getName()));
            if (!confirmed) {
                return "Abort";
            }
        }
        
        try {
            restUtils.sendDeleteCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName(),
                    authenticationUtils.getMap()).get("body");
            statusCommand.setExitStatut(0);
            return MessageFormat.format(APPLICATION_REMOVED, application.getName());
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        } finally {
            setApplication(null);
        }
    }

    public String startApp(String applicationName) {
        checkApplication(applicationName);

        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("applicationName", application.getName());
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + urlLoader.start,
                    authenticationUtils.getMap(), parameters).get("body");
            statusCommand.setExitStatut(0);
            return MessageFormat.format(APPLICATION_STARTED, application.getName());
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }
    }

    public String stopApp(String applicationName) {
        checkApplication(applicationName);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("applicationName", application.getName());

        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + urlLoader.stop,
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }
        statusCommand.setExitStatut(0);

        return MessageFormat.format(APPLICATION_STOPPED, application.getName());
    }

    public List<Application> listAllApps() throws ManagerResponseException {
        List<Application> listApplications;
        String json = null;

        try {
            json = (String) restUtils.sendGetCommand(authenticationUtils.finalHost + urlLoader.listAllApplications,
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }

        listApplications = JsonConverter.getApplications(json);
        statusCommand.setExitStatut(0);
        return listApplications;
    }

    public String listAll() {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();

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
        checkApplication(null);
        
        String body = "";

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
                params.putAll(authenticationUtils.getMap());
                body = (String) restUtils.sendPostForUpload(authenticationUtils.finalHost
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
        checkApplication(applicationName);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("applicationName", application.getName());
        parameters.put("alias", alias);
        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + "/alias",
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }
        statusCommand.setExitStatut(0);
        return "An alias has been successfully added to " + application.getName();
    }

    public String listAllAliases(String applicationName) {
        checkApplication(applicationName);
        
        String response = null;

        try {
            response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/alias",
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        MessageConverter.buildListAliases(JsonConverter.getAliases(response));

        statusCommand.setExitStatut(0);

        return JsonConverter.getAliases(response).size() + " aliases found!";
    }

    public String removeAlias(String applicationName, String alias) {
        checkApplication(applicationName);
        
        try {

            restUtils.sendDeleteCommand(authenticationUtils.finalHost + urlLoader.actionApplication
                    + application.getName() + "/alias/" + alias, authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        statusCommand.setExitStatut(0);

        return "This alias has successful been deleted";
    }

    @Deprecated
    public String checkAndRejectIfError(String applicationName) {
        if (authenticationUtils.isConnected()) {
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
        
        if (applicationName != null) {
            log.log(Level.INFO, applicationName);
            return useApplication(applicationName);
        }

        return null;
    }
    
    public String createEnvironmentVariable(String applicationName, String key, String value) {
        checkApplication(applicationName);
        
        Guard.guardTrue(StringUtils.isNotEmpty(key), "No key was given");
        Guard.guardTrue(Pattern.matches("^[a-zA-Z][-a-zA-Z0-9_]*$", key), "Invalid key name \"{0}\"", key);
        
        try {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("keyEnv", key);
            parameters.put("valueEnv", value);

            restUtils.sendPostCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables",
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        statusCommand.setExitStatut(0);
        return MessageFormat.format("Environment variable \"{0}\" has been added to application \"{1}\"",
                key,
                application.getName());
    }

    private EnvironmentVariable getEnvironmentVariable(String key) throws ManagerResponseException {
        String response = restUtils.sendGetCommand(
                authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                        + "/container/" + application.getServer().getName() + "/environmentVariables",
                authenticationUtils.getMap()).get("body");

        List<EnvironmentVariable> environmentVariables = JsonConverter.getEnvironmentVariables(response);
        
        EnvironmentVariable variable = environmentVariables.stream()
                .filter(var -> var.getKeyEnv().equals(key))
                .findAny().orElseThrow(() -> new CloudUnitCliException(MessageFormat.format(NO_SUCH_ENV_VAR, key)));
        return variable;
    }

    public String removeEnvironmentVariable(String applicationName, String key) {
        checkApplication(applicationName);

        try {
            EnvironmentVariable variable = getEnvironmentVariable(key);
            int id = variable.getId();

            restUtils.sendDeleteCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables/" + id,
                    authenticationUtils.getMap()).get("body");
        } catch (Exception e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        statusCommand.setExitStatut(0);
        return MessageFormat.format(ENV_VAR_REMOVED, key, application.getName());
    }

    public String updateEnvironmentVariable(String applicationName, String oldKey, String newKey, String value) {
        checkApplication(applicationName);

        try {
            EnvironmentVariable variable = getEnvironmentVariable(oldKey);
            int id = variable.getId();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("keyEnv", newKey);
            parameters.put("valueEnv", value);

            restUtils.sendPutCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables/" + id,
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        statusCommand.setExitStatut(0);

        return "This environment variable has successful been updated";
    }

    public String listAllEnvironmentVariables(String applicationName) {
        checkApplication(applicationName);
        
        try {
            String response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables",
                    authenticationUtils.getMap()).get("body");
            
            MessageConverter.buildListEnvironmentVariables(JsonConverter.getEnvironmentVariables(response));

            statusCommand.setExitStatut(0);
            return JsonConverter.getEnvironmentVariables(response).size() + " variables found!";
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

    }

    public String listContainers(String applicationName) {
        checkApplication(applicationName);

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
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/command",
                    authenticationUtils.getMap()).get("body");

        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        MessageConverter.buildListCommands(JsonConverter.getCommands(response));

        statusCommand.setExitStatut(0);

        return JsonConverter.getCommands(response).size() + " commands found!";
    }

    public String execCommand(String name, String containerName, String arguments) {

        if (containerName == null) {
            if (getApplication() == null) {
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
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/command/" + name + "/exec",
                    authenticationUtils.getMap(), entity);

        } catch (ManagerResponseException | JsonProcessingException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }
        statusCommand.setExitStatut(0);

        return "The command " + name + " has been executed";
    }
}

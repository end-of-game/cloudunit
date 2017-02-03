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
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private static final String ENV_VAR_ADDED = "Environment variable \"{0}\" has been added to application \"{1}\"";
    private static final String ENV_VAR_REMOVED = "Environment variable \"{0}\" has been removed from application \"{1}\"";
    private static final String NO_SUCH_ENV_VAR = "No such environment variable \"{0}\"";
    private static final String APPLICATION_CREATED = "Application \"{0}\" has been created";
    private static final String APPLICATION_REMOVED = "Application \"{0}\" has been removed";
    private static final String APPLICATION_STARTED = "Application \"{0}\" has been started";
    private static final String APPLICATION_STOPPED = "Application \"{0}\" has been stopped";
    private static final String NO_APPLICATION = Messages.getString("application.NO_APPLICATION");
    private static final String NO_SUCH_APPLICATION = Messages.getString("application.NO_SUCH_APPLICATION");
    private static final String ALIAS_COUNT = Messages.getString("application.ALIAS_COUNT");
    private static final String ALIAS_ADDED = Messages.getString("application.ALIAS_ADDED");
    private static final String ALIAS_REMOVED = Messages.getString("application.ALIAS_REMOVED");

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

    private Application currentApplication;

    public Application getCurrentApplication() {
        return currentApplication;
    }

    public void setCurrentApplication(Application application) {
        this.currentApplication = application;
    }

    public boolean isApplicationSelected() {
        return currentApplication != null;
    }
    
    public void checkApplicationSelected() {
        Guard.guardTrue(isApplicationSelected(), NO_APPLICATION);
        currentApplication = getApplication(currentApplication.getName());
    }
    
    public void checkConnectedAndApplicationSelected() {
        authenticationUtils.checkConnected();
        checkApplicationSelected();
        fileUtils.checkNotInFileExplorer();
    }

    public boolean applicationExists(String applicationName) {
        try {
            return listAllApps().stream()
                    .map(app -> app.getName())
                    .filter(name -> name.equals(applicationName))
                    .findAny()
                    .isPresent();
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't check for application", e);
        }
    }
    
    public void checkApplicationExists(String applicationName) {
        Guard.guardTrue(applicationExists(applicationName), NO_SUCH_APPLICATION, applicationName);
    }

    public String getInformations() {
        checkConnectedAndApplicationSelected();

        useApplication(currentApplication.getName());
        String dockerManagerIP = authenticationUtils.finalHost;
        statusCommand.setExitStatut(0);

        MessageConverter.buildApplicationMessage(currentApplication, dockerManagerIP);
        return "Terminated";
    }
    
    public Application getApplication(String applicationName) {
        String result;
        try {
            String url = authenticationUtils.finalHost + urlLoader.actionApplication + applicationName;
            result = restUtils.sendGetCommand(url, authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't get application", e);
        }

        if (StringUtils.isNotBlank(result)) {
            return JsonConverter.getApplication(result);
        } else {
            throw new CloudUnitCliException(MessageFormat.format(NO_SUCH_APPLICATION, applicationName));
        }        
    }
    
    public Application getSpecificOrCurrentApplication(String applicationName) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();
        
        if (StringUtils.isEmpty(applicationName)) {
            checkApplicationSelected();
            return currentApplication;
        } else {
            checkApplicationExists(applicationName);
            return getApplication(applicationName);
        }
    }

    public String useApplication(String applicationName) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();

        currentApplication = getApplication(applicationName);
        return MessageFormat.format("Using application \"{0}\"", currentApplication.getName());
    }
    
    public String createApp(String applicationName, String serverName) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();

        try {
            checkUtils.checkImageExists(serverName);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("applicationName", applicationName);
            parameters.put("serverName", serverName);

            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication,
                    authenticationUtils.getMap(), parameters).get("body");

            useApplication(applicationName);
            return MessageFormat.format(APPLICATION_CREATED, currentApplication.getName());
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't create application", e);
        }
    }

    public String rmApp(String applicationName, boolean errorIfNotExists, Prompter prompter) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();
        
        Application application = null;
        
        if (StringUtils.isEmpty(applicationName)) {
            checkApplicationSelected();
            
            application = currentApplication;
        } else {
            if (errorIfNotExists) {
                checkApplicationExists(applicationName);
            } else if (!applicationExists(applicationName)) {
                return MessageFormat.format(NO_SUCH_APPLICATION, applicationName);
            }
            
            application = getApplication(applicationName);
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
            
            if (application.equals(currentApplication)) {
                currentApplication = null;
            }
            
            return MessageFormat.format(APPLICATION_REMOVED, application.getName());
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't remove application", e);
        }
    }

    public String startApp(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("applicationName", application.getName());

        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + urlLoader.start,
                    authenticationUtils.getMap(), parameters).get("body");

        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't start application", e);
        }
        
        return MessageFormat.format(APPLICATION_STARTED, application.getName());
    }

    public String stopApp(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("applicationName", application.getName());

        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + urlLoader.stop,
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        return MessageFormat.format(APPLICATION_STOPPED, application.getName());
    }

    public List<Application> listAllApps() throws ManagerResponseException {
        String json = null;
        try {
            json = (String) restUtils.sendGetCommand(authenticationUtils.finalHost + urlLoader.listAllApplications,
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't list applications", e);
        }

        List<Application> listApplications = JsonConverter.getApplications(json);
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
        checkConnectedAndApplicationSelected();
        
        String body = "";

        Guard.guardTrue(path != null, "Please specify a file path");
        
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
                    + urlLoader.actionApplication + currentApplication.getName() + "/deploy", params).get("body");
        } catch (IOException e) {
            throw new CloudUnitCliException("The file could not be opened", e);
        }

        if (StringUtils.isNotEmpty(body) && openBrowser) {
            DesktopAPI.browse(URI.create(currentApplication.getLocation()));
        }

        return MessageFormat.format("Application deployed. Access on {0}", currentApplication.getLocation());
    }

    public String addNewAlias(String applicationName, String alias) {
        Application application = getSpecificOrCurrentApplication(applicationName);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("applicationName", application.getName());
        parameters.put("alias", alias);
        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + "/alias",
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't add new alias", e);
        }
        return MessageFormat.format(ALIAS_ADDED,
                alias,
                application.getName());
    }

    public String listAllAliases(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        String response = null;

        try {
            response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/alias",
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't list aliases", e);
        }

        MessageConverter.buildListAliases(JsonConverter.getAliases(response));

        return MessageFormat.format(ALIAS_COUNT, JsonConverter.getAliases(response).size());
    }

    public String removeAlias(String applicationName, String alias) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        try {

            restUtils.sendDeleteCommand(authenticationUtils.finalHost + urlLoader.actionApplication
                    + application.getName() + "/alias/" + alias, authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }

        statusCommand.setExitStatut(0);

        return MessageFormat.format(ALIAS_REMOVED, alias, application.getName());
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

        if (currentApplication == null && applicationName == null) {
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
        Application application = getSpecificOrCurrentApplication(applicationName);
        
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
            throw new CloudUnitCliException("Couldn't create environment variable", e);
        }

        return MessageFormat.format(ENV_VAR_ADDED, key, application.getName());
    }

    private EnvironmentVariable getEnvironmentVariable(Application application, String key) throws ManagerResponseException {
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
        Application application = getSpecificOrCurrentApplication(applicationName);

        try {
            EnvironmentVariable variable = getEnvironmentVariable(application, key);
            int id = variable.getId();

            restUtils.sendDeleteCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables/" + id,
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't remove environment variable", e);
        }

        return MessageFormat.format(ENV_VAR_REMOVED, key, application.getName());
    }

    public String updateEnvironmentVariable(String applicationName, String oldKey, String newKey, String value) {
        Application application = getSpecificOrCurrentApplication(applicationName);

        try {
            EnvironmentVariable variable = getEnvironmentVariable(application, oldKey);
            int id = variable.getId();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("keyEnv", newKey);
            parameters.put("valueEnv", value);

            restUtils.sendPutCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables/" + id,
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("", e); 
        }

        statusCommand.setExitStatut(0);

        return "This environment variable has successful been updated";
    }

    public String listAllEnvironmentVariables(String applicationName, boolean export) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        try {
            String response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables",
                    authenticationUtils.getMap()).get("body");
            
            List<EnvironmentVariable> variables = JsonConverter.getEnvironmentVariables(response);
            
            if (export) {
                return envVarExportScript(variables);
            } else {
                MessageConverter.buildListEnvironmentVariables(variables);
                return JsonConverter.getEnvironmentVariables(response).size() + " variables found!";
            }
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't list environment variables", e);
        }
    }

    private String envVarExportScript(List<EnvironmentVariable> variables) {
        return variables.stream()
            .map(v -> String.format("export %s=%s", v.getKeyEnv(), v.getValueEnv()))
            .collect(Collectors.joining("\n"));
    }

    public String listContainers(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);

        List<String> containers = new ArrayList<>();
        containers.add(application.getServer().getName());

        for (Module module : application.getModules()) {
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
                    authenticationUtils.finalHost + urlLoader.actionApplication + currentApplication.getName()
                            + "/container/" + currentApplication.getServer().getName() + "/command",
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
            if (getCurrentApplication() == null) {
                statusCommand.setExitStatut(1);
                return ANSIConstants.ANSI_RED
                        + "No application is currently selected by the following command line : use <application name>"
                        + ANSIConstants.ANSI_RESET;
            }
            containerName = getCurrentApplication().getServer().getName();
        }

        try {
            Command command = new Command();
            command.setName(name);
            command.setArguments(Arrays.asList(arguments.split(",")));
            ObjectMapper objectMapper = new ObjectMapper();
            String entity = objectMapper.writeValueAsString(command);
            restUtils.sendPostCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + currentApplication.getName()
                            + "/container/" + currentApplication.getServer().getName() + "/command/" + name + "/exec",
                    authenticationUtils.getMap(), entity);

        } catch (ManagerResponseException | JsonProcessingException e) {
            statusCommand.setExitStatut(1);
            return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
        }
        statusCommand.setExitStatut(0);

        return "The command " + name + " has been executed";
    }
}

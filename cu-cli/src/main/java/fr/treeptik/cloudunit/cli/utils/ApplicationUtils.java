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
    private static final String NO_SUCH_ENV_VAR = "No such environment variable \"{0}\"";
    private static final String NO_APPLICATION = Messages.getString("application.NO_APPLICATION");
    private static final String NO_SUCH_APPLICATION = Messages.getString("application.NO_SUCH_APPLICATION");

    @InjectLogger
    private Logger log;

    @Autowired
    private UrlLoader urlLoader;

    @Autowired
    private AuthenticationUtils authenticationUtils;

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
        return listAllApps().stream()
                .map(app -> app.getName())
                .filter(name -> name.equals(applicationName))
                .findAny()
                .isPresent();
    }
    
    public void checkApplicationExists(String applicationName) {
        Guard.guardTrue(applicationExists(applicationName), NO_SUCH_APPLICATION, applicationName);
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

    public Application useApplication(String applicationName) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();

        currentApplication = getApplication(applicationName);
        return currentApplication;
    }
    
    public Application createApp(String applicationName, String serverName) {
        authenticationUtils.checkConnected();
        fileUtils.checkNotInFileExplorer();

        try {
            checkUtils.checkImageExists(serverName);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("applicationName", applicationName);
            parameters.put("serverName", serverName);

            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication,
                    authenticationUtils.getMap(), parameters).get("body");

            return useApplication(applicationName);
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't create application", e);
        }
    }

    public boolean rmApp(String applicationName, boolean errorIfNotExists, Prompter prompter) {
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
                return false;
            }
            
            application = getApplication(applicationName);
        }
        
        if (prompter != null) {
            boolean confirmed = prompter.promptConfirmation(MessageFormat.format("Remove application \"{0}\"?",
                    application.getName()));
            if (!confirmed) {
                return false;
            }
        }
        
        try {
            restUtils.sendDeleteCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName(),
                    authenticationUtils.getMap()).get("body");
            
            if (application.equals(currentApplication)) {
                currentApplication = null;
            }
            
            return true;
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't remove application", e);
        }
    }

    public void startApp(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("applicationName", application.getName());

        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + urlLoader.start,
                    authenticationUtils.getMap(), parameters).get("body");

        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't start application", e);
        }
    }

    public void stopApp(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("applicationName", application.getName());

        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.actionApplication + urlLoader.stop,
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't stop application", e);
        }
    }

    public List<Application> listAllApps() {
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

    public void deployFromAWar(File path, boolean openBrowser) throws MalformedURLException, URISyntaxException {
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
    }

    public void addNewAlias(String applicationName, String alias) {
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
    }

    public List<String> listAllAliases(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        String response = null;

        try {
            response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName() + "/alias",
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't list aliases", e);
        }
        
        return JsonConverter.getAliases(response);
    }

    public void removeAlias(String applicationName, String alias) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        try {

            restUtils.sendDeleteCommand(authenticationUtils.finalHost + urlLoader.actionApplication
                    + application.getName() + "/alias/" + alias, authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't remove alias", e);
        }
    }
    
    public void createEnvironmentVariable(String applicationName, String key, String value) {
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

    public void removeEnvironmentVariable(String applicationName, String key) {
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
    }

    public void updateEnvironmentVariable(String applicationName, String oldKey, String newKey, String value) {
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
    }

    public List<EnvironmentVariable> listAllEnvironmentVariables(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);
        
        try {
            String response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + application.getName()
                            + "/container/" + application.getServer().getName() + "/environmentVariables",
                    authenticationUtils.getMap()).get("body");
            
            return JsonConverter.getEnvironmentVariables(response);
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't list environment variables", e);
        }
    }

    public List<String> listContainers(String applicationName) {
        Application application = getSpecificOrCurrentApplication(applicationName);

        List<String> containers = new ArrayList<>();
        containers.add(application.getServer().getName());

        for (Module module : application.getModules()) {
            containers.add(module.getName());
        }
        return containers;
    }

    public List<Command> listCommands(String containerName) {
        String response;

        try {
            response = restUtils.sendGetCommand(
                    authenticationUtils.finalHost + urlLoader.actionApplication + currentApplication.getName()
                            + "/container/" + currentApplication.getServer().getName() + "/command",
                    authenticationUtils.getMap()).get("body");

        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't list commands.", e);
        }

        return JsonConverter.getCommands(response);
    }

    public void execCommand(String name, String containerName, String arguments) {
        if (containerName == null) {
            checkConnectedAndApplicationSelected();
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
            throw new CloudUnitCliException("Couldn't execute command.", e);
        }
    }
}

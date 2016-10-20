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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.model.Module;

@Component
public class ModuleUtils {
    private static final String MODULES_COUNT = Messages.getString("module.MODULES_COUNT");
    private static final String MODULE_REMOVED = Messages.getString("module.MODULE_REMOVED");
    private static final String MODULE_ADDED = Messages.getString("module.MODULE_ADDED");
    private static final String NO_MODULES = Messages.getString("module.NO_MODULES");
    private static final String NO_SUCH_MODULE = Messages.getString("module.NO_SUCH_MODULE");

    @Autowired
    private ApplicationUtils applicationUtils;

    @Autowired
    private AuthenticationUtils authenticationUtils;
    
    @Autowired
    private CheckUtils checkUtils;

    @Autowired
    private UrlLoader urlLoader;

    @InjectLogger
    private Logger log;

    @Autowired
    private ShellStatusCommand statusCommand;

    @Autowired
    private RestUtils restUtils;

    public String getListModules() {
        applicationUtils.checkConnectedAndApplicationSelected();
        
        String dockerManagerIP = applicationUtils.getCurrentApplication().getManagerIp();
        statusCommand.setExitStatut(0);
        MessageConverter.buildLightModuleMessage(applicationUtils.getCurrentApplication(), dockerManagerIP);

        int size = applicationUtils.getCurrentApplication().getModules().size();
        return MessageFormat.format(MODULES_COUNT, size);
    }

    public String addModule(final String imageName, final File script) {
        applicationUtils.checkConnectedAndApplicationSelected();
        
        checkUtils.checkImageExists(imageName);
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("imageName", imageName);
        parameters.put("applicationName", applicationUtils.getCurrentApplication().getName());
        
        try {
            restUtils.sendPostCommand(authenticationUtils.finalHost + urlLoader.modulePrefix,
                    authenticationUtils.getMap(), parameters).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't add module", e);
        }
        
        return MessageFormat.format(MODULE_ADDED,
                imageName,
                applicationUtils.getCurrentApplication().getName());
    }

    public String removeModule(String moduleName) {
        applicationUtils.checkConnectedAndApplicationSelected();

        Module module = findModule(moduleName);
        
        try {
            restUtils.sendDeleteCommand(
                    authenticationUtils.finalHost + urlLoader.modulePrefix
                            + applicationUtils.getCurrentApplication().getName() + "/" + module.getName(),
                    authenticationUtils.getMap()).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't remove module", e);
        }

        return MessageFormat.format(MODULE_REMOVED,
                moduleName,
                applicationUtils.getCurrentApplication().getName());
    }

    private Module findModule(String moduleName) {
        Guard.guardTrue(!applicationUtils.getCurrentApplication().getModules().isEmpty(), NO_MODULES,
                applicationUtils.getCurrentApplication().getName());
        
        Optional<Module> module = applicationUtils.getCurrentApplication().getModules().stream()
                .filter(m -> m.getName().endsWith(moduleName))
                .findAny();
        
        Guard.guardTrue(module.isPresent(), NO_SUCH_MODULE,
                applicationUtils.getCurrentApplication().getName(),
                moduleName);
        
        return module.get();
    }

    public String managePort(String moduleName, final String port, final Boolean open) {
        applicationUtils.checkConnectedAndApplicationSelected();
        
        Module module = findModule(moduleName);
        try {
            restUtils.sendPutCommand(
                    authenticationUtils.finalHost + urlLoader.modulePrefix + "/" + module.getId(),
                    authenticationUtils.getMap(), new HashMap<String, String>() {
                        private static final long serialVersionUID = 1L;
                        {
                            put("publishPort", open.toString());
                            put("port", port);
                        }
                    }).get("body");
        } catch (ManagerResponseException e) {
            throw new CloudUnitCliException("Couldn't change port", e);
        }

        return "OK";
    }

    public String runScript(String moduleName, File file) {
        applicationUtils.checkConnectedAndApplicationSelected();
        
        Module module = findModule(moduleName);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("file", new FileSystemResource(file));
        parameters.putAll(authenticationUtils.getMap());
        
        String url = String.format("%s%s%s/run-script",
            authenticationUtils.finalHost,
            urlLoader.modulePrefix,
            module.getName());
        
        log.info("Running script...");
        
        restUtils.sendPostForUpload(url, parameters);
        
        return "Done";
    }

}

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
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.Messages;
import fr.treeptik.cloudunit.cli.utils.ModuleUtils;
import fr.treeptik.cloudunit.model.Module;

@Component
public class ModuleCommands implements CommandMarker {
    private static final String MODULE_REMOVED = Messages.getString("module.MODULE_REMOVED");
    private static final String MODULE_ADDED = Messages.getString("module.MODULE_ADDED");

    private static final String HELP_MODULE_NAME =
            "Name of the module. Use show-modules command to get all modules of this application";

    private static final String HELP_MODULE_TYPE = "Module type \n "
            + " MYSQL 5.5 : -name mysql-5-5\n"
            + " MYSQL 5.6 : -name mysql-5-6\n"
            + " MYSQL 5.7 : -name mysql-5-7\n"
            + " POSTGRES 9.3 : -name postgresql-9-3\n"
            + " POSTGRES 9.4 : -name postgresql-9-4\n"
            + " POSTGRES 9.5 : -name postgresql-9-5\n"
            + " REDIS 3.0 : -name redis-3-0\n"
            + " Mongo 2.6 : -name mongo-2-6";
    
    @Autowired
    private ModuleUtils moduleUtils;
    
    @Autowired
    private CliFormatter formatter;

    @CliCommand(value = "add-module", help = "Add a new module to the current application")
    public String addModule(
            @CliOption(key = "name", mandatory = true, help = HELP_MODULE_TYPE) String moduleName) {
        moduleUtils.addModule(moduleName, null);
        
        return formatter.unlessQuiet(MessageFormat.format(MODULE_ADDED, moduleName));
    }

    @CliCommand(value = "rm-module", help = "Remove a module from the current application")
    public String removeModule(
            @CliOption(key = "name", mandatory = true, help = HELP_MODULE_NAME) String moduleName) {
        moduleUtils.removeModule(moduleName);
        
        return formatter.unlessQuiet(MessageFormat.format(MODULE_REMOVED, moduleName));
    }

    @CliCommand(value = "expose-port", help = "Expose the default module port")
    public String exposePort(
            @CliOption(key = "name", mandatory = true, help = HELP_MODULE_NAME) String moduleName,
            @CliOption(key = "port", mandatory = true, help = "Port number") String port) {
        moduleUtils.managePort(moduleName, port, true);
        return null;
    }

    @CliCommand(value = "close-port", help = "Expose the default module port")
    public String closePort(
            @CliOption(key = "name", mandatory = true, help = HELP_MODULE_NAME) String moduleName,
            @CliOption(key = { "port" }, mandatory = true, help = "Port number") String port) {
        moduleUtils.managePort(moduleName, port, false);
        return null;
    }

    @CliCommand(value = "list-modules", help = "Display information about all modules of the current application")
    public String listModules() {
        List<Module> modules = moduleUtils.getListModules();
        List<String> moduleNames = modules.stream()
                .map(m -> m.getName())
                .collect(Collectors.toList());
        return formatter.list(moduleNames);
    }
    
    @CliCommand(value = "run-script", help = "Run a script inside a module of the current application")
    public String runScript(
            @CliOption(key = "name", mandatory = true, help = HELP_MODULE_NAME) String moduleName,
            @CliOption(key = "path", mandatory = true, help = "Script path") File file) {
        moduleUtils.runScript(moduleName, file);
        
        return formatter.unlessQuiet("Script run");
    }

}

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

import fr.treeptik.cloudunit.cli.utils.ModuleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ModuleCommands implements CommandMarker {

    @Autowired
    private ModuleUtils moduleUtils;

    @CliCommand(value = "add-module", help = "Add a new module to the current application")
    public String addModule(@CliOption(key = { "name" }, mandatory = true, help = "Module type "
            + "\n MYSQL 5.5 : -name mysql-5-5 " + "\n MYSQL 5.6 : -name mysql-5-6 " + "\n MYSQL 5.7 : -name mysql-5-7 "
            + "\n POSTGRES 9.3 : -name postgresql-9-3 " + "\n POSTGRES 9.4 : -name postgresql-9-4 "
            + "\n POSTGRES 9.5 : -name postgresql-9-5 " + "\n REDIS 3.0 : -name redis-3-0 "
            + "\nMongo 2.6 : -name mongo-2-6") String moduleName) {

        return moduleUtils.addModule(moduleName, null);
    }

    @CliCommand(value = "rm-module", help = "Remove a module from the current application")
    public String removeModule(@CliOption(key = {
            "name" }, mandatory = true, help = "Name of the module. Use show-modules command to get all modules of this application") String moduleName) {
        return moduleUtils.removeModule(moduleName);
    }

    @CliCommand(value = "expose-port", help = "Expose the default module port")
    public String exposePort(@CliOption(key = {
            "name" }, mandatory = true, help = "Name of the module. Use show-modules command to get all modules of this application") String moduleName) {
        return moduleUtils.managePort(moduleName, true);
    }

    @CliCommand(value = "close-port", help = "Expose the default module port")
    public String closePort(@CliOption(key = {
            "name" }, mandatory = true, help = "Name of the module. Use show-modules command to get all modules of this application") String moduleName) {
        return moduleUtils.managePort(moduleName, false);
    }

    @CliCommand(value = "display-modules", help = "Display informations about all modules of the current application")
    public String getApplication() {
        return moduleUtils.getListModules();
    }

}

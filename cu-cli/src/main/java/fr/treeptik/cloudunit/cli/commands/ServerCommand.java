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

import fr.treeptik.cloudunit.cli.utils.ServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ServerCommand implements CommandMarker {

    @Autowired
    private ServerUtils serverUtils;

    @CliCommand(value = "change-jvm-memory", help = "Change memory of the application server")
    public String changeMemory(
            @CliOption(key = {"size"}, mandatory = true, help = "Available memory size (Mo) {512, 1024, 2048, 3072}") String memorySize) {
        return serverUtils.changeMemory(memorySize);
    }

    @CliCommand(value = "add-jvm-option", help = "Add a new java option to the application server")
    public String addOpts(
            @CliOption(key = {""}, mandatory = true, help = "Add your jvm opts (excepted memory values) between \"\"") String opts) {
        return serverUtils.addOpts(opts);
    }

    @CliCommand(value = "change-java-version", help = "Change java version")
    public String changeJavaVersion(
            @CliOption(key = {""}, mandatory = false, help = "Application name") String applicationName,
            @CliOption(key = {"javaVersion"}, mandatory = true, help = "Choose your java version (available : jdk1.7.0_55 & jdk1.8.0_25)") String javaVersion) {
        return serverUtils.changeJavaVersion(applicationName, javaVersion);
    }

    @CliCommand(value = "open-port", help = "Change java version")
    public String openPort(
            @CliOption(key = {"", "name"}, mandatory = false, help = "Application name") String applicationName,
            @CliOption(key = {"port"}, mandatory = true, help = "Choose a port to open") String portToOpen,
            @CliOption(key = {"nature"}, mandatory = true, help = "Choose a port to open") String portNature) {
        return serverUtils.openPort(applicationName, portToOpen, portNature);
    }

    @CliCommand(value = "remove-port", help = "Change java version")
    public String openPort(
            @CliOption(key = {"", "name"}, mandatory = false, help = "Application name") String applicationName,
            @CliOption(key = {"port"}, mandatory = true, help = "Choose a port to open") String portToOpen) {
        return serverUtils.removePort(applicationName, portToOpen);
    }

}

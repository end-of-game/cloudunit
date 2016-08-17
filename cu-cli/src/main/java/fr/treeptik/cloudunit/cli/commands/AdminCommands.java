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

import fr.treeptik.cloudunit.cli.utils.AdminUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class AdminCommands implements CommandMarker {

    @Autowired
    private AdminUtils adminUtils;

    //@CliCommand(value = "su-display-users", help = "Display all users")
    public String getAllUsers() {
        return adminUtils.listUsers();
    }

    // @CliCommand(value = "su-create-user", help = "Create a new user")
    public String createUser(
            @CliOption(key = "login", mandatory = true, help = "Your login with alphanumeric chars") String login,
            @CliOption(key = "firstName", mandatory = true) String firstName,
            @CliOption(key = "lastName", mandatory = true) String lastName,
            @CliOption(key = "organization", mandatory = true, help = "Your organization with alphanumeric chars") String organization,
            @CliOption(key = "email", mandatory = true) String email,
            @CliOption(key = "password", mandatory = true) String password) {
        return adminUtils.createUser(login, firstName, lastName, organization,
                email, password);
    }

    //@CliCommand(value = "su-rm-user", help = "Remove an user")
    public String removeUser(
            @CliOption(key = "login", mandatory = true) String login) {
        return adminUtils.deleteUser(login);
    }

    //  @CliCommand(value = "su-change-rights", help = "Change rights for a user")
    public String changeRightsUser(
            @CliOption(key = "login", mandatory = true) String login,
            @CliOption(key = "role", mandatory = true, help = "Available roles : admin/user") String role) {
        return adminUtils.changeRightsUser(login, role);
    }

    //   @CliCommand(value = "su-messages", help = "Get all users messages")
    public String getMessages(
            @CliOption(key = "login", mandatory = false, help = "Account login") String login,
            @CliOption(key = "rows", mandatory = false, help = "Number of messages to show") String rows) {
        return adminUtils.getMessages(login, rows);
    }

}

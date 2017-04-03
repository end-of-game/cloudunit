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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.utils.AuthenticationUtils;

@Component
public class UserCommands implements CommandMarker {
	@Autowired
	private AuthenticationUtils authenticationUtils;
	
	@Autowired
	private CliFormatter formatter;

	@CliCommand(value = "connect", help = "Connect to CloudUnit Manager")
	public String connect(
	        @CliOption(key = "login", mandatory = true, help = "Login") String login,
			@CliOption(key = "password", mandatory = false, help = "Password",
			    unspecifiedDefaultValue = "") String password,
			@CliOption(key = "host", mandatory = false, help = "URL for Cloudunit Manager",
			    unspecifiedDefaultValue = "") String host) {
	    authenticationUtils.connect(login, password, host, new CliPrompter());
		return formatter.unlessQuiet("Connected");
	}

	@CliCommand(value = "disconnect", help = "Disconnect")
	public String disconnect() {
	    authenticationUtils.disconnect();
		return formatter.unlessQuiet("Disconnected");
	}

}
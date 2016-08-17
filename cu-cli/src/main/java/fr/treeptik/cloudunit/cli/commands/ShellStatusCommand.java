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

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class ShellStatusCommand implements CommandMarker {

    private Integer exitStatut;

    public ShellStatusCommand() {
        this.exitStatut = 0;
    }

    @CliCommand(value = "echo", help = "Get exit code of the last executed CU command")
    public Integer echo(@CliOption(key = {"s"}) String element) {
        return exitStatut;
    }


    public Integer getExitStatut() {
        return exitStatut;
    }

    /**
     * Functionnal status = 0 / error status = 1
     *
     * @param exitStatut
     */
    public void setExitStatut(Integer exitStatut) {
        this.exitStatut = exitStatut;
    }

}

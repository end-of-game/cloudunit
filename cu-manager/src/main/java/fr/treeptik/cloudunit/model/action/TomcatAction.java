/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Snapshot;

public class TomcatAction
    extends ServerAction {

    private static final long serialVersionUID = 1L;

    public TomcatAction(Server parent) {
        super(parent);
    }

    @Override
    public String getServerManagerPath() {
        return "/manager/html?";
    }

    @Override
    public String getServerManagerPort() {
        return "8080";
    }

    @Override
    public String getServerPort() {
        return "8080";
    }

    @Override
    public Snapshot cloneProperties(Snapshot snapshot) {
        snapshot.setType(parent.getImage().getName());
        snapshot.setJvmRelease(parent.getJvmRelease());
        snapshot.setJvmOptions(parent.getJvmOptions());
        snapshot.setJvmMemory(parent.getJvmMemory());
        return snapshot;
    }

    @Override
    public String cleanCommand() {
        return null;
    }

    @Override
    public String getLogLocation() {
        return "cloudunit/appconf/logs";
    }

}

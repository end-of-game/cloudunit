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

import java.io.Serializable;

public abstract class ServerAction
    implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Server parent;

    public ServerAction(Server parent) {
        this.parent = parent;
    }

    public abstract String getServerManagerPath();

    public abstract String getServerManagerPort();

    public abstract String getServerPort();

    public abstract Snapshot cloneProperties(Snapshot snapshot);

    public abstract String cleanCommand();

    public abstract String getLogLocation();

}

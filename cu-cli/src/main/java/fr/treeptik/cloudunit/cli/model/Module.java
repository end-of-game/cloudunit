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

package fr.treeptik.cloudunit.cli.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Module extends Container implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> moduleInfos = new HashMap<>();

    private String managerLocation;

    private String cuInstanceName;

    public Module() {
    }

    public Map<String, String> getModuleInfos() {
        return moduleInfos;
    }

    public void setModuleInfos(Map<String, String> moduleInfos) {
        this.moduleInfos = moduleInfos;
    }

    public String getManagerLocation() {
        return managerLocation;
    }

    public void setManagerLocation(String managerLocation) {
        this.managerLocation = managerLocation;
    }

    public String getCuInstanceName() {
        return cuInstanceName;
    }

    public void setCuInstanceName(String cuInstanceName) {
        this.cuInstanceName = cuInstanceName;
    }

    @Override
    public String toString() {
        return "Module [id=" + id + ", startDate=" + startDate + ", name="
                + name + ", cloudId=" + containerID + ", memorySize="
                + memorySize + ", containerID=" + containerIP
                + ", moduleInfos="
                + moduleInfos + ", listPorts=" + listPorts + ", image=" + image
                + ", status=" + status + "]";
    }
}

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

import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.ModuleConfiguration;
import fr.treeptik.cloudunit.utils.HipacheRedisUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class ModuleAction
    implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String managerLocation;

    protected Module module;

    public ModuleAction(Module module) {
        this.module = module;
    }

    public abstract void initModuleInfos();

    public abstract String getInitDataCmd()
        throws IOException;

    public abstract List<String> createDockerCmd(String databasePassword, String envExec);

    public abstract List<String> createDockerCmdForClone(Map<String, String> map);

    /**
     * add url to access to module manager (e.g phpMyAdmin)
     *
     * @param hipacheRedisUtils
     * @param parent
     * @param instanceNumber
     * @return
     */
    public abstract Module enableModuleManager(
        HipacheRedisUtils hipacheRedisUtils, Module parent, Long instanceNumber);

    public abstract void updateModuleManager(
        HipacheRedisUtils hipacheRedisUtils);

    public abstract void unsubscribeModuleManager(
        HipacheRedisUtils hipacheRedisUtils);

    public abstract ModuleConfiguration cloneProperties();

    public abstract String getLogLocation();

    public abstract String getManagerLocation(String subdomain, String suffix);

}

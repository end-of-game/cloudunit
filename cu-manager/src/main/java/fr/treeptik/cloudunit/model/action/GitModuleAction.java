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
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GitModuleAction
    extends ModuleAction {

    private static final long serialVersionUID = 1L;

    public GitModuleAction(Module module) {
        super(module);
    }

    @Override
    public void initModuleInfos() {
        // TODO Auto-generated method stub
    }

    @Override
    public List<String> createDockerCmd(String databasePassword, String envExec) {
        return Arrays.asList("/bin/sh", "/cloudunit/scripts/start-service.sh",
            module.getApplication().getUser().getLogin(),
            module.getApplication().getUser().getPassword(),
            module.getApplication().getRestHost(),
            module.getApplication().getServers().get(0).getContainerIP(),
            module.getApplication().getName(),
            databasePassword,
            envExec);
    }

    @Override
    public void unsubscribeModuleManager(HipacheRedisUtils hipacheRedisUtils) {
    }

    @Override
    public String getInitDataCmd() {
        return null;
    }

    @Override
    public Module enableModuleManager(HipacheRedisUtils hipacheRedisUtils,
                                      Module module, Long instanceNumber) {
        return module;
    }

    @Override
    public void updateModuleManager(HipacheRedisUtils hipacheRedisUtils) {
    }

    @Override
    public ModuleConfiguration cloneProperties() {
        ModuleConfiguration moduleConfiguration = new ModuleConfiguration();
        moduleConfiguration.setName("git");
        return moduleConfiguration;
    }

    @Override
    public List<String> createDockerCmdForClone(Map<String, String> map,
                                                String databasePassword, String envExec) {
        return new ArrayList<>();
    }

    @Override
    public String getLogLocation() {
        return null;
    }

    @Override
    public String getManagerLocation(String subdomain, String suffix) {
        return "";
    }

}

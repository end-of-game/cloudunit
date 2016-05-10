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

package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.ModuleConfiguration;
import fr.treeptik.cloudunit.utils.HipacheRedisUtils;
import fr.treeptik.cloudunit.utils.ModuleUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApacheModuleAction
    extends ModuleAction {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MANAGER_PORT = "80";

    public ApacheModuleAction(Module module) {
        super(module);
    }

    @Override
    public void initModuleInfos() {
        module.getModuleInfos().put("database", module.getApplication().getName().toLowerCase());
        module.getModuleInfos().put("dockerManagerAddress", module.getApplication().getManagerIp());
        module.getModuleInfos().putAll(ModuleUtils.generateRamdomUserAccess());
    }

    @Override
    public List<String> createDockerCmd(String databasePassword, String envExec, String databaseHostname) {
        return Arrays.asList(
            module.getModuleInfos().get("username"),
            module.getModuleInfos().get("password"),
            module.getModuleInfos().get("database"),
            module.getApplication().getUser().getPassword(),
            module.getApplication().getUser().getLogin(),
            databasePassword,
            envExec, databaseHostname);
    }

    @Override
    public void unsubscribeModuleManager(HipacheRedisUtils hipacheRedisUtils) {
        hipacheRedisUtils.removePhpMyAdminKey(
            module.getApplication(),
            module.getImage().getManagerName(),
            Long.parseLong(module.getName().substring(module.getName().lastIndexOf("-") + 1)));
    }

    @Override
    public String getInitDataCmd() {
        return null;
    }

    @Override
    public Module enableModuleManager(HipacheRedisUtils hipacheRedisUtils,
                                      Module module, Long instanceNumber) {
        hipacheRedisUtils.createModuleManagerKey(
            module.getApplication(),
            module.getContainerIP(),
            DEFAULT_MANAGER_PORT,
            module.getImage().getManagerName(),
            instanceNumber);
        return module;
    }

    @Override
    public void updateModuleManager(HipacheRedisUtils hipacheRedisUtils) {
        hipacheRedisUtils.updatedAdminAddress(
            module.getApplication(),
            module.getContainerIP(),
            DEFAULT_MANAGER_PORT,
            module.getImage().getManagerName(),
            Long.parseLong(module.getName().substring(module.getName().lastIndexOf("-") + 1)));
    }

    @Override
    public ModuleConfiguration cloneProperties() {
        ModuleConfiguration moduleConfiguration = new ModuleConfiguration();
        moduleConfiguration.setName(module.getImage().getName());
        moduleConfiguration.setPath(module.getImage().getPath() + "-" + module.getInstanceNumber());
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("username-" + module.getImage().getName(), module.getModuleInfos().get("username"));
        properties.put("password-" + module.getImage().getName(), module.getModuleInfos().get("password"));
        properties.put("database-" + module.getImage().getName(), module.getModuleInfos().get("database"));
        moduleConfiguration.setProperties(properties);
        return moduleConfiguration;
    }

    @Override
    public List<String> createDockerCmdForClone(
        Map<String, String> map,
        String databasePassword,
        String envExec, String databaseHostname) {
        return Arrays.asList(
            map.get("username"),
            map.get("password"),
            module.getModuleInfos().get("database"),
            module.getApplication().getUser().getPassword(),
            module.getApplication().getUser().getLogin(),
            databasePassword,
            envExec, databaseHostname);
    }

    @Override
    public String getLogLocation() {
        return null;
    }

    @Override
    public String getManagerLocation(String subdomain, String suffix) {
        return null;
    }

}

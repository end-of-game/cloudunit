package fr.treeptik.cloudunit.model.action;/*
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

import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.ModuleConfiguration;
import fr.treeptik.cloudunit.utils.KeyValueStoreUtils;
import fr.treeptik.cloudunit.utils.ModuleUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgreSQLModuleAction
    extends ModuleAction {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MANAGER_PORT = "80";

    public PostgreSQLModuleAction(Module module) {
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
    public void unsubscribeModuleManager(KeyValueStoreUtils hipacheRedisUtils) {
        hipacheRedisUtils.removePhpMyAdminKey(
            module.getApplication(),
            module.getImage().getManagerName(),
            Long.parseLong(module.getName().substring(module.getName().lastIndexOf("-") + 1)));
    }

    @Override
    public String getInitDataCmd() {
        String command = "sh /cloudunit/scripts/init-data.sh "
            + module.getModuleInfos().get("username") + " "
            + module.getModuleInfos().get("password") + " "
            + module.getApplication().getName();
        return command;
    }

    @Override
    public Module enableModuleManager(KeyValueStoreUtils hipacheRedisUtils,
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
    public void updateModuleManager(KeyValueStoreUtils hipacheRedisUtils) {
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
        String managerLocation = "http://"
            + module.getImage().getManagerName()
            + module.getName().substring(module.getName().lastIndexOf("-") + 1) + "-"
            + module.getApplication().getName() + "-"
            + module.getApplication().getUser().getLogin() + "-"
            + module.getApplication().getUser().getOrganization()
            + subdomain + suffix + "/" + module.getImage().getManagerName();
        return managerLocation;
    }

}

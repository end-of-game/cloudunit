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
import fr.treeptik.cloudunit.utils.ModuleUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisModuleAction extends ModuleAction {

	public RedisModuleAction(Module module) {
		super(module);
	}

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MANAGER_PORT = "8081";

	@Override
	public void initModuleInfos() {
		module.getModuleInfos().put("dockerManagerAddress",
				module.getApplication().getManagerIp());
		module.getModuleInfos().putAll(ModuleUtils.generateRamdomUserAccess());
	}

	@Override
	public String getInitDataCmd() throws IOException {
		return "";
	}

	@Override
	public List<String> createDockerCmd() {
		return Arrays.asList(module.getApplication().getUser().getPassword(),
				module.getApplication().getRestHost(), module.getApplication()
						.getUser().getLogin(),
				module.getModuleInfos().get("password"), module.getModuleInfos().get("username"));
	}

	@Override
	public List<String> createDockerCmdForClone(Map<String, String> map) {
		return Arrays.asList(module.getApplication().getUser().getPassword(),
				module.getApplication().getRestHost(), module.getApplication()
						.getUser().getLogin(), map.get("password"), module.getModuleInfos().get("username"));
	}

	@Override
	public Module enableModuleManager(HipacheRedisUtils hipacheRedisUtils,
			Module module, Long instanceNumber) {
		hipacheRedisUtils.createModuleManagerKey(module.getApplication(), module.getContainerIP(), DEFAULT_MANAGER_PORT, module
				.getImage().getManagerName(), instanceNumber);
		return module;
	}

	@Override
	public void updateModuleManager(HipacheRedisUtils hipacheRedisUtils) {
		hipacheRedisUtils.updatedAdminAddress(module.getApplication(), module.getContainerIP(), DEFAULT_MANAGER_PORT, module
				.getImage().getManagerName(), Long.parseLong(module.getName()
				.substring(module.getName().lastIndexOf("-") + 1)));

	}

	@Override
	public void unsubscribeModuleManager(HipacheRedisUtils hipacheRedisUtils) {
		hipacheRedisUtils.removePhpMyAdminKey(module.getApplication(), module
				.getImage().getManagerName(), Long.parseLong(module.getName()
				.substring(module.getName().lastIndexOf("-") + 1)));
	}

	@Override
	public ModuleConfiguration cloneProperties() {
		ModuleConfiguration moduleConfiguration = new ModuleConfiguration();
		moduleConfiguration.setName(module.getImage().getName());
		moduleConfiguration.setPath(module.getImage().getPath() + "-"
				+ module.getInstanceNumber() + "-data");
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("password-" + module.getImage().getName(), module
				.getModuleInfos().get("password"));
properties.put("username-" + module.getImage().getName(), module
				.getModuleInfos().get("username"));
		moduleConfiguration.setProperties(properties);
		return moduleConfiguration;
	}

	@Override
	public String getLogLocation() {
		return null;
	}

	@Override
	public String getManagerLocation(String subdomain, String suffix) {
		String managerLocation = "http://"
				+ module.getImage().getManagerName()
				+ module.getName().substring(
				module.getName().lastIndexOf("-") + 1) + "-"
				+ module.getApplication().getName() + "-"
				+ module.getApplication().getUser().getLogin() + "-"
				+ module.getApplication().getUser().getOrganization()
				+ subdomain + suffix + "/" ;
		return managerLocation;
	}

}

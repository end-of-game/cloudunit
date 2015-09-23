package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.ModuleConfiguration;
import fr.treeptik.cloudunit.utils.HipacheRedisUtils;
import fr.treeptik.cloudunit.utils.ModuleUtils;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgreSQLModuleAction extends ModuleAction {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MANAGER_PORT = "80";

	public PostgreSQLModuleAction(Module module) {
		super(module);
	}

	@Override
	public void initModuleInfos() {

		module.getModuleInfos().put("database",
				module.getApplication().getName().toLowerCase());
		module.getModuleInfos().put("dockerManagerAddress",
				module.getApplication().getManagerHost());

		module.getModuleInfos().putAll(ModuleUtils.generateRamdomUserAccess());

	}

	@Override
	public List<String> createDockerCmd() {
		return Arrays.asList(module.getModuleInfos().get("username"), module
				.getModuleInfos().get("password"),
				module.getModuleInfos().get("database"), module
						.getApplication().getUser().getPassword());
	}

	@Override
	public void unsubscribeModuleManager(HipacheRedisUtils hipacheRedisUtils) {
		hipacheRedisUtils.removePhpMyAdminKey(module.getApplication(), module
				.getImage().getManagerName(), Long.parseLong(module.getName()
				.substring(module.getName().lastIndexOf("-") + 1)));

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
	public Module enableModuleManager(HipacheRedisUtils hipacheRedisUtils,
			Module module, Environment env, Long instanceNumber) {
		hipacheRedisUtils.createModuleManagerKey(module.getApplication(), env
				.getProperty("redis.ip"), module.getContainerIP(), DEFAULT_MANAGER_PORT, module
				.getImage().getManagerName(), instanceNumber);
		return module;
	}

	@Override
	public void updateModuleManager(HipacheRedisUtils hipacheRedisUtils,
			Environment env) {
		hipacheRedisUtils.updatedAdminAddress(module.getApplication(), env
				.getProperty("redis.ip"), module.getContainerIP(), DEFAULT_MANAGER_PORT, module
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
		properties.put("username-" + module.getImage().getName(), module
				.getModuleInfos().get("username"));
		properties.put("password-" + module.getImage().getName(), module
				.getModuleInfos().get("password"));
		properties.put("database-" + module.getImage().getName(), module
				.getModuleInfos().get("database"));
		moduleConfiguration.setProperties(properties);
		return moduleConfiguration;
	}

	@Override
	public List<String> createDockerCmdForClone(Map<String, String> map) {
		return Arrays.asList(map.get("username"), map.get("password"),
				module.getModuleInfos().get("database"), module
						.getApplication().getUser().getPassword(), module
						.getApplication().getRestHost(), module.getApplication().getUser().getLogin());
	}

	@Override
	public String getLogLocation() {
		// TODO Auto-generated method stub
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
				+ subdomain + suffix + "/"+ module.getImage().getManagerName();
		return managerLocation;
	}

}

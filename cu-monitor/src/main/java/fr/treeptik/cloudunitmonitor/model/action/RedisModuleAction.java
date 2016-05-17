package fr.treeptik.cloudunitmonitor.model.action;

import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;
import org.springframework.core.env.Environment;

import fr.treeptik.cloudunitmonitor.model.Module;
import fr.treeptik.cloudunitmonitor.utils.HipacheRedisUtils;

public class RedisModuleAction extends ModuleAction {

	public RedisModuleAction(Module parent) {
		super(parent);
	}
	private static final String DEFAULT_MANAGER_PORT = "8081";

	private static final long serialVersionUID = 1L;

	@Override
	public Module enableModuleManager(HipacheRedisUtils hipacheRedisUtils,
			Module parent, Environment env, Long instanceNumber) {
		String redisIp = ApplicationEntryPoint.IP_REDIS;
		hipacheRedisUtils.createModuleManagerKey(parent.getApplication(), redisIp, parent.getContainerIP(), DEFAULT_MANAGER_PORT, parent
				.getImage().getManagerName(), instanceNumber);
		return parent;
	}

	@Override
	public void updateModuleManager(HipacheRedisUtils hipacheRedisUtils,
			Environment env) {
		String redisIp = ApplicationEntryPoint.IP_REDIS;
		hipacheRedisUtils.updateAdminAddress(parent.getApplication(), redisIp, parent.getContainerIP(), DEFAULT_MANAGER_PORT, parent
				.getImage().getManagerName(), Long.parseLong(parent.getName()
				.substring(parent.getName().lastIndexOf("-") + 1)));
	}

}

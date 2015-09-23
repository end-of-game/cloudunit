package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.ModuleConfiguration;
import fr.treeptik.cloudunit.utils.HipacheRedisUtils;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GitModuleAction extends ModuleAction {

	private static final long serialVersionUID = 1L;

	public GitModuleAction(Module module) {
		super(module);
	}

	@Override
	public void initModuleInfos() {
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> createDockerCmd() {
		return Arrays.asList("/bin/sh", "/cloudunit/scripts/start-service.sh",
				module.getApplication().getUser().getLogin(), module
						.getApplication().getUser().getPassword(), module
						.getApplication().getRestHost(), module
						.getApplication().getServers().get(0).getContainerIP(),
				module.getApplication().getName());
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
			Module module, Environment env, Long instanceNumber) {
		return module;
	}

	@Override
	public void updateModuleManager(HipacheRedisUtils hipacheRedisUtils,
			Environment env) {
	}

	@Override
	public ModuleConfiguration cloneProperties() {
		ModuleConfiguration moduleConfiguration = new ModuleConfiguration();
		moduleConfiguration.setName("git");
		return moduleConfiguration;
	}

	@Override
	public List<String> createDockerCmdForClone(Map<String, String> map) {
		return new ArrayList<>();
	}

	@Override
	public String getLogLocation() {
		return null;
	}

	@Override
	public String getManagerLocation(String subdomain, String suffix) {
		return "http://www.google.fr";
	}

}

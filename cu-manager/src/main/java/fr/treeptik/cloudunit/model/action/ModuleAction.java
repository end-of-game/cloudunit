package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.ModuleConfiguration;
import fr.treeptik.cloudunit.utils.HipacheRedisUtils;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class ModuleAction implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String managerLocation;

	protected Module module;

	public ModuleAction(Module module) {
		this.module = module;
	}

	public abstract void initModuleInfos();

	public abstract String getInitDataCmd() throws IOException;

	public abstract List<String> createDockerCmd();

	public abstract List<String> createDockerCmdForClone(Map<String, String> map);

	/**
	 * 
	 * add url to access to module manager (e.g phpMyAdmin)
	 * 
	 * @param hipacheRedisUtils
	 * @param parent
	 * @param env
	 * @param instanceNumber
	 * @return
	 */
	public abstract Module enableModuleManager(
			HipacheRedisUtils hipacheRedisUtils, Module parent,
			Environment env, Long instanceNumber);

	public abstract void updateModuleManager(
			HipacheRedisUtils hipacheRedisUtils, Environment env);

	public abstract void unsubscribeModuleManager(
			HipacheRedisUtils hipacheRedisUtils);

	public abstract ModuleConfiguration cloneProperties();

	public abstract String getLogLocation();

	public abstract String getManagerLocation(String subdomain, String suffix);

}

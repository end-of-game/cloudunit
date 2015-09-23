package fr.treeptik.cloudunit.model;

import fr.treeptik.cloudunit.model.action.*;

public class ModuleFactory {

	/**
	 * Return new module with ModuleAction initialized
	 * 
	 * @param imageName
	 * @return
	 */
	public static Module getModule(String imageName) {
		Module module = new Module();
		module.setModuleAction(getModuleAction(imageName, module));
		return module;
	}

	/**
	 * Update module with ModuleAction initialized
	 * 
	 * @param module
	 * @return
	 */
	public static Module updateModule(Module module) {
		module.setModuleAction(getModuleAction(module.getImage().getName(),	module));
		return module;
	}

	private static ModuleAction getModuleAction(String imageName, Module module) {

		ModuleAction result = null;
		if (imageName.toLowerCase().contains("mysql")) {
			result = new MysqlModuleAction(module);
		} else if (imageName.toLowerCase().contains("postgresql")) {
			result = new PostgreSQLModuleAction(module);
		} else if (imageName.toLowerCase().contains("git")) {
			result = new GitModuleAction(module);
		} else if (imageName.toLowerCase().contains("redis")) {
			result = new RedisModuleAction(module);
		} else if (imageName.toLowerCase().contains("mongo")) {
			result = new MongoModuleAction(module);
		}

		return result;

	}

}
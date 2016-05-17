package fr.treeptik.cloudunitmonitor.model;

import fr.treeptik.cloudunitmonitor.model.action.ModuleAction;
import fr.treeptik.cloudunitmonitor.model.action.MongoModuleAction;
import fr.treeptik.cloudunitmonitor.model.action.MysqlModuleAction;
import fr.treeptik.cloudunitmonitor.model.action.PostgreSQLModuleAction;
import fr.treeptik.cloudunitmonitor.model.action.RedisModuleAction;

public class ModuleFactory {

	/**
	 * Return new module with ModuleAction initialized
	 * 
	 * @param moduleName
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
	 * @param moduleName
	 * @return
	 */
	public static Module updateModule(Module module) {

		module.setModuleAction(getModuleAction(module.getImage().getName(), module));

		return module;
	}

	private static ModuleAction getModuleAction(String imageName, Module module) {

		ModuleAction result = null;

		if (imageName.toLowerCase().contains("mysql")) {
			result = new MysqlModuleAction(module);
		} else if (imageName.toLowerCase().contains("postgresql")) {
			result = new PostgreSQLModuleAction(module);
		} else if (imageName.toLowerCase().contains("mongo")) {
			result = new MongoModuleAction(module);
		} else if (imageName.toLowerCase().contains("redis")) {
			result = new RedisModuleAction(module);
		}

		return result;

	}

}
package fr.treeptik.cloudunit.model;

import fr.treeptik.cloudunit.model.action.JBossAction;
import fr.treeptik.cloudunit.model.action.JBossAction5;
import fr.treeptik.cloudunit.model.action.ServerAction;
import fr.treeptik.cloudunit.model.action.TomcatAction;

public class ServerFactory {

	/**
	 * Return new module with ModuleAction initialized
	 * 
	 * @param moduleName
	 * @return
	 */
	public static Server getServer(String imageName) {

		Server server = new Server();

		server.setServerAction(getServerAction(imageName, server));

		return server;
	}

	/**
	 * Update module with ModuleAction initialized
	 * 
	 * @param moduleName
	 * @return
	 */
	public static Server updateServer(Server server) {
		server.setServerAction(getServerAction(server.getImage().getName(),
				server));
		return server;
	}

	private static ServerAction getServerAction(String imageName, Server server) {

		ServerAction result = null;

		// TODO : HACK TO REMOVE MODULE VERSION

		if (imageName.toLowerCase().contains("tomcat")) {
			result = new TomcatAction(server);
		} else if (imageName.toLowerCase().contains("jboss-5")) {
			result = new JBossAction5(server);
		} else if (imageName.toLowerCase().contains("jboss")) {
			result = new JBossAction(server);
		}

		return result;

	}

}

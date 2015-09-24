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

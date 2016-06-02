/*
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

package fr.treeptik.cloudunit.model;

import fr.treeptik.cloudunit.model.action.*;

public class ServerFactory {

    /**
     * Return new module with ModuleAction initialized
     *
     * @param
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
     * @param
     * @return
     */
    public static Server updateServer(Server server) {
        server.setServerAction(getServerAction(server.getImage().getName(),
                server));
        return server;
    }

    private static ServerAction getServerAction(String imageName, Server server) {

        ServerAction result = null;
        if (imageName.toLowerCase().contains("tomcat")) {
            result = new TomcatAction(server);
        } else if (imageName.toLowerCase().contains("jboss-5")) {
            result = new JBossAction5(server);
        } else if (imageName.toLowerCase().contains("jboss")) {
            result = new JBossAction(server);
        } else if (imageName.toLowerCase().contains("fatjar")) {
            result = new FatJarAction(server);
        } else if (imageName.toLowerCase().contains("apache")) {
            result = new ApacheAction(server);
        } else  {
            result = new GenericAction(server);
        }
        return result;

    }

}

/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the GPL is right for you,
 * you can always test our software under the GPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.modules.activemq;

import fr.treeptik.cloudunit.modules.AbstractModuleControllerTestIT;

/**
 * Created by guillaume on 01/10/16.
 */
public class Wildfly10ActiveMQ5132ModuleControllerTestIT extends AbstractModuleControllerTestIT {

    public Wildfly10ActiveMQ5132ModuleControllerTestIT() {
        super.server = "wildfly-10";
        super.module = "activemq-5.13";
        super.numberPort = "61616";
        super.managerPrefix = "";
        super.managerSuffix = "";
        super.managerPageContent = "";
    }

    @Override
    protected void checkConnection(String forwardedPort) throws Exception {
        new CheckBrokerConnection().invoke(forwardedPort, "ACTIVEMQ_ADMIN_LOGIN",
                "ACTIVEMQ_ADMIN_PASSWORD", "ACTIVEMQ_NAME", "JMS");
    }
}

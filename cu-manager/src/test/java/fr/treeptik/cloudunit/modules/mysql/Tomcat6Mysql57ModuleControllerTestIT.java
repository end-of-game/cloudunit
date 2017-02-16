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

package fr.treeptik.cloudunit.modules.mysql;

import fr.treeptik.cloudunit.modules.AbstractModuleControllerTestIT;

/**
 * Created by nicolas on 04/10/15.
 */
public class Tomcat6Mysql57ModuleControllerTestIT extends AbstractModuleControllerTestIT {

    public Tomcat6Mysql57ModuleControllerTestIT() {
        super.server = "tomcat-6";
        super.module = "mysql-5-7";
        super.numberPort = "3306";
        super.managerPrefix = "phpmyadmin";
        super.managerSuffix = "phpmyadmin";
        super.managerPageContent = "phpMyAdmin";
        super.testScriptPath = "src/test/resources/mysql/test.sql";    }

    @Override
    protected void checkConnection(String forwardedPort) throws Exception {
        new CheckDatabaseConnection().invoke(forwardedPort, "MYSQL_USER",
                "MYSQL_PASSWORD", "MYSQL_DATABASE", "com.mysql.jdbc.Driver", "jdbc:mysql://");
    }
}

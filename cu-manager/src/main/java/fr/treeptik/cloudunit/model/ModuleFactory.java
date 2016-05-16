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
        module.setModuleAction(getModuleAction(module.getImage().getName(), module));
        return module;
    }

    private static ModuleAction getModuleAction(String imageName, Module module) {

        ModuleAction result = null;
        if (imageName.toLowerCase().contains("mysql")) {
            result = new MysqlModuleAction(module);
        } else if (imageName.toLowerCase().contains("postgresql") || imageName.toLowerCase().contains("postgis")) {
            result = new PostgreSQLModuleAction(module);
        } else if (imageName.toLowerCase().contains("redis")) {
            result = new RedisModuleAction(module);
        } else if (imageName.toLowerCase().contains("mongo")) {
            result = new MongoModuleAction(module);
        }

        return result;

    }

}
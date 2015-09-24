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

package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.model.Module;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO : reprendre pour factoriser les méthodes et les objets. Etendre Module
 * en fonction de la BDD
 * 
 */
public class ModuleUtils {


	/**
	 * Ajoute des informations spécifiques pour un module MEMCACHED
	 * 
	 * @param module
	 * @return
	 */
	public static Module addMemcachedSpecificInformations(Module module) {
		module.getModuleInfos().put("linkAlias", "memcached1");
		return module;
	}

	/**
	 * Ajoute des informations spécifiques pour un module ORACLE
	 * 
	 * @param module
	 * @return
	 */
	public static Module addOracleSpecificInformations(Module module) {
		module.getModuleInfos().put("database",
				module.getApplication().getName().toLowerCase());
		module.getModuleInfos().putAll(generateRamdomUserAccess());
		return module;
	}

	/**
	 * Génére un couple login/motdepasse indépendant de la BDD
	 * 
	 * @return
	 */
	public static Map<String, String> generateRamdomUserAccess() {
		Map<String, String> map = new HashMap<String, String>();
		SecureRandom random = new SecureRandom();
		String username = "admin"
				+ new BigInteger(130, random).toString(32).substring(2, 10);
		String password = new BigInteger(130, random).toString(32).substring(2,
				10);
		map.put("username", username);
		map.put("password", password);
		return map;
	}

}

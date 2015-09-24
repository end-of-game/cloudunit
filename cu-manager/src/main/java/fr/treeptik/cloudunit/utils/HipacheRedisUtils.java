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

import fr.treeptik.cloudunit.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;

@Component
public class HipacheRedisUtils {

	@Inject
	private Environment env;

	private Logger logger = LoggerFactory.getLogger(HipacheRedisUtils.class);

	/**
	 * Add two keys into redis for application server and management console
	 *
	 * @param application
	 * @param redisIp
	 * @param dockerManagerIP
	 * @param serverPort
	 * @param serverManagerPort
	 */
	public void createRedisAppKey(Application application, String redisIp,
			String dockerManagerIP, String serverPort, String serverManagerPort) {
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));
		String suffixCloudUnit = application.getSuffixCloudUnitIO();
		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();

			String subNameSpace = concatSubNameSpace(application);

			String key = subNameSpace + suffixCloudUnit;
			String frontend = "frontend:" + key.toLowerCase();
			jedis.rpush(frontend, key.toLowerCase());
			jedis.rpush(frontend, "http://" + dockerManagerIP + ":"
					+ serverPort);

			/*
			 * CREATE AN ENTRY FOR SERVER MANAGER
			 */

			String frontendServerManager = "frontend:manager-"
					+ key.toLowerCase();
			jedis.rpush(frontendServerManager, key.toLowerCase());
			jedis.rpush(frontendServerManager, "http://" + dockerManagerIP
					+ ":" + serverManagerPort);

		} catch (JedisConnectionException | UnsupportedEncodingException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	/**
	 * Write a new alias into redis database
	 *
	 * @param alias
	 * @param application
	 * @param redisIp
	 * @param serverPort
	 */
	public void writeNewAlias(String alias, Application application,
			String redisIp, String serverPort) {

		String dockerManagerIP = application.getServers().get(0)
				.getContainerIP();
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));

		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();
			logger.info("ALIAS VALUE IN ADD NEW ALIAS : " + alias);
			alias = alias + application.getSuffixCloudUnitIO();
			String frontend = "frontend:" + alias.toLowerCase();
			jedis.rpush(frontend, alias.toLowerCase());
			jedis.rpush(frontend, "http://" + dockerManagerIP + ":"
					+ serverPort);
		} catch (JedisConnectionException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}

	}

	/**
	 * Update the alias
	 *
	 * @param alias
	 * @param application
	 * @param redisIp
	 * @param serverPort
	 */
	public void updateAlias(String alias, Application application,
			String redisIp, String serverPort) {

		String dockerManagerIP = application.getManagerIP();
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));

		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();
			alias = alias + application.getSuffixCloudUnitIO();
			String frontend = "frontend:" + alias.toLowerCase();
			jedis.lset(frontend, 1, "http://" + dockerManagerIP + ":"
					+ serverPort);
		} catch (JedisConnectionException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	/**
	 * Update the server address
	 *
	 * @param application
	 * @param redisIp
	 * @param dockerManagerIP
	 * @param serverPort
	 * @param serverManagerPort
	 */
	public void updateServerAddress(Application application, String redisIp,
			String dockerManagerIP, String serverPort, String serverManagerPort) {
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));
		String suffixCloudUnit = application.getSuffixCloudUnitIO();

		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();

			String subNameSpace = concatSubNameSpace(application);

			String key = subNameSpace + suffixCloudUnit;
			String frontend = "frontend:" + key.toLowerCase();
			jedis.lset(frontend, 1, "http://" + dockerManagerIP + ":"
					+ serverPort);

			/*
			 * UPDATE THE ENTRY FOR SERVER MANAGER
			 */

			String frontendServerManager = "frontend:manager-"
					+ key.toLowerCase();
			jedis.lset(frontendServerManager, 1, "http://" + dockerManagerIP
					+ ":" + serverManagerPort);
		} catch (JedisConnectionException | UnsupportedEncodingException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	/**
	 * Remove the server address
	 *
	 * @param application
	 * @param redisIp
	 */
	public void removeServerAddress(Application application, String redisIp) {
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));
		String suffixCloudUnit = application.getSuffixCloudUnitIO();

		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();

			String subNameSpace = concatSubNameSpace(application);
			String key = subNameSpace + suffixCloudUnit;
			String frontend = "frontend:" + subNameSpace + suffixCloudUnit;

			jedis.rpop(frontend.toLowerCase());

			/*
			 * REMOVE THE ENTRY FOR SERVER MANAGER
			 */

			String frontendServerManager = "frontend:manager-"
					+ key.toLowerCase();
			jedis.del(frontendServerManager);

		} catch (JedisConnectionException | UnsupportedEncodingException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	/**
	 * Remove the redis application key
	 *
	 * @param application
	 * @param redisIp
	 */
	public void removeRedisAppKey(Application application, String redisIp) {

		int redisPort = Integer.parseInt(env.getProperty("redis.port"));

		String suffixCloudUnit = application.getSuffixCloudUnitIO();
		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();

			String subNameSpace = concatSubNameSpace(application);

			String frontend = "frontend:" + subNameSpace + suffixCloudUnit;
			jedis.del(frontend.toLowerCase());
		} catch (JedisConnectionException | UnsupportedEncodingException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	public void createModuleManagerKey(Application application, String redisIp,
			String dockerContainerIP, String modulePort,
			String cloudunitModuleManagerSuffix, Long instanceNumber) {

		if (logger.isInfoEnabled()) {
			logger.info("parameters : [ " + application.getName()
					+ " - dockerContainerIP : " + dockerContainerIP
					+ " - redisIp : " + redisIp
					+ " - cloudunitModuleManagerSuffix : "
					+ cloudunitModuleManagerSuffix + " - instanceNumber : "
					+ instanceNumber);
		}

		int redisPort = Integer.parseInt(env.getProperty("redis.port"));

		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();

			String subNameSpace = concatSubNameSpace(application);

			String alias = cloudunitModuleManagerSuffix + instanceNumber + "-"
					+ subNameSpace + application.getSuffixCloudUnitIO();
			String frontend = "frontend:" + alias;
			String valeur = "http://" + dockerContainerIP + ":" + modulePort;
			if (logger.isInfoEnabled()) {
				logger.info("Ajout dans Redis de [" + frontend + "] --> " + "["
						+ valeur + "]");
			}
			jedis.rpush(frontend.toLowerCase(), alias.toLowerCase());
			jedis.rpush(frontend.toLowerCase(), valeur.toLowerCase());
		} catch (JedisConnectionException | UnsupportedEncodingException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	public void updatedAdminAddress(Application application, String redisIp,
			String dockerManagerIP, String modulePort,
			String cloudunitModuleManagerSuffix, Long instanceNumber) {
		if (logger.isInfoEnabled()) {
			logger.info("parameters : [ " + application.getName()
					+ " - dockerManagerIP : " + dockerManagerIP
					+ " - redisIp : " + redisIp
					+ " - cloudunitModuleManagerSuffix : "
					+ cloudunitModuleManagerSuffix + " - instanceNumber : "
					+ instanceNumber);
		}
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));
		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();

			String subNameSpace = concatSubNameSpace(application);
			String alias = cloudunitModuleManagerSuffix + instanceNumber + "-"
					+ subNameSpace + application.getSuffixCloudUnitIO();
			String frontend = "frontend:" + alias;
			String valeur = "http://" + dockerManagerIP + ":" + modulePort;
			if (logger.isInfoEnabled()) {
				logger.info("Mise à jour dans Redis de [" + frontend + "] --> "
						+ "[" + valeur + "]");
			}
			jedis.lset(frontend.toLowerCase(), 1, valeur);
		} catch (JedisConnectionException | UnsupportedEncodingException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	public void removePhpMyAdminKey(Application application,
			String cloudunitModuleManagerSuffix, Long instanceNumber) {
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));
		String redisIp = env.getProperty("redis.ip");
		JedisPool pool = null;
		Jedis jedis = null;

		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();

			String subNameSpace = concatSubNameSpace(application);

			String alias = cloudunitModuleManagerSuffix + instanceNumber + "-"
					+ subNameSpace + application.getSuffixCloudUnitIO();
			String frontend = "frontend:" + alias.toLowerCase();
			jedis.del(frontend);
			if (logger.isInfoEnabled()) {
				logger.info("Suppression dans Redis de [" + frontend + "]");
			}
		} catch (JedisConnectionException | UnsupportedEncodingException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	public void removeAlias(String alias) {
		int redisPort = Integer.parseInt(env.getProperty("redis.port"));
		String redisIp = env.getProperty("redis.ip");
		JedisPool pool = null;
		Jedis jedis = null;
		try {
			pool = new JedisPool(new JedisPoolConfig(), redisIp, redisPort,
					3000);
			jedis = pool.getResource();
			String frontend = "frontend:" + alias.toLowerCase()
					+ env.getProperty("suffix.cloudunit.io");
			jedis.del(frontend);
			if (logger.isInfoEnabled()) {
				logger.info("Suppression dans Redis de [" + frontend + "]");
			}
		} catch (JedisConnectionException e) {
			logger.error("HipacheRedisUtils Exception", e);
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				pool.returnResource(jedis);
				pool.destroy();
			}
		}
	}

	/**
	 * concat Name of application + "-" + Login of User + "-" + Organization
	 * 
	 * @param application
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String concatSubNameSpace(Application application)
			throws UnsupportedEncodingException {
		String subNameSpace = AlphaNumericsCharactersCheckUtils
				.convertToAlphaNumerics(application.getName())
				+ "-"
				+ AlphaNumericsCharactersCheckUtils
						.convertToAlphaNumerics(application.getUser()
								.getLogin())
				+ "-"
				+ AlphaNumericsCharactersCheckUtils
						.convertToAlphaNumerics(application.getUser()
								.getOrganization());

		return subNameSpace;

	}

}

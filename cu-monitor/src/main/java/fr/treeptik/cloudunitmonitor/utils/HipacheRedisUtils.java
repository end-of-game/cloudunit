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


package fr.treeptik.cloudunitmonitor.utils;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.utils.KeyValueStoreUtils;
import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Management for Redis.
 * Redis is used with Hipache DotCloud ReverseProxy
 */
@Component
public class HipacheRedisUtils implements KeyValueStoreUtils {

    private Logger logger = LoggerFactory.getLogger(HipacheRedisUtils.class);

    @Inject
    private Environment env;

    /**
     * Add two keys into redis for application server and management console
     *
     * @param application
     * @param dockerManagerIP
     * @param serverPort
     * @param serverManagerPort
     */
    @Override
    public void createRedisAppKey(Application application,
                                  String dockerManagerIP, String serverPort, String serverManagerPort) {
        String suffixCloudUnit = System.getenv("CU_SUB_DOMAIN") +  env.getProperty("suffix.cloudunit.io");
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS,
                    Integer.parseInt(env.getProperty("redis.port")),
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

        } catch (Exception e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
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
     * @param serverPort
     */
    @Override
    public void writeNewAlias(String alias, Application application, String serverPort) {

        String dockerManagerIP = application.getServers().get(0)
                .getContainerIP();

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();
            logger.info("ALIAS VALUE IN ADD NEW ALIAS : " + alias);
            String frontend = "frontend:" + alias.toLowerCase();
            jedis.rpush(frontend, alias.toLowerCase());
            jedis.rpush(frontend, "http://" + dockerManagerIP + ":"
                    + serverPort);
        } catch (JedisConnectionException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
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
     * @param serverPort
     */
    @Override
    public void updateAlias(String alias, Application application, String serverPort) {

        String dockerManagerIP = application.getManagerIp();

        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();
            String frontend = "frontend:" + alias.toLowerCase();
            jedis.lset(frontend, 1, "http://" + dockerManagerIP + ":"
                    + serverPort);
        } catch (JedisConnectionException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }

    /**
     * Update the server address
     *
     * @param application
     * @param dockerManagerIP
     * @param serverPort
     * @param serverManagerPort
     */
    @Override
    public void updateServerAddress(Application application,
                                    String dockerManagerIP, String serverPort, String serverManagerPort) {

        String suffixCloudUnit =  env.getProperty("suffix.cloudunit.io");
        if (System.getenv("CU_SUB_DOMAIN") != null) {
            suffixCloudUnit = System.getenv("CU_SUB_DOMAIN") + suffixCloudUnit;
        }

        JedisPool pool = null;
        Jedis jedis = null;
        try {

            String subNameSpace = concatSubNameSpace(application);
            String key = subNameSpace + suffixCloudUnit;
            String frontend = "frontend:" + key.toLowerCase();

            logger.info("enter in method : " + frontend);
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();

            jedis.lset(frontend, 1, "http://" + dockerManagerIP + ":"
                    + serverManagerPort);

			/*
             * UPDATE THE ENTRY FOR SERVER MANAGER
			 */

            String frontendServerManager = "frontend:manager-"
                    + key.toLowerCase();
            jedis.lset(frontendServerManager, 1, "http://" + dockerManagerIP
                    + ":" + serverManagerPort);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
            // Exit for non blocking
            System.exit(1);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }

    /**
     * Remove the server address
     *
     * @param application
     */
    @Override
    public void removeServerAddress(Application application) {
        String suffixCloudUnit = env.getProperty("suffix.cloudunit.io");
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = new JedisPool(new JedisPoolConfig(), ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
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
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }

    @Override
    public void updatePortAlias(
            String serverIP, Integer port, String portAlias) {


        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();

            String frontend = "frontend:" + portAlias;
            jedis.lset(frontend, 1, "http://" + serverIP + ":"
                    + port);

        } catch (JedisConnectionException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }


    /**
     * Remove the server address
     * portAlias
     */
    @Override
    public void removeServerPortAlias(String portAlias) {
        JedisPool pool = new JedisPool(new JedisPoolConfig(),ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
        Jedis jedis = pool.getResource();
        try {
            String frontend = "frontend:" + portAlias;
            logger.info(frontend);
            jedis.del(frontend.toLowerCase());
        } catch (JedisConnectionException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }

    /**
     * Remove the redis application key
     *
     * @param application
     */
    @Override
    public void removeRedisAppKey(Application application) {

        String suffixCloudUnit = env.getProperty("suffix.cloudunit.io");
        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();

            String subNameSpace = concatSubNameSpace(application);

            String frontend = "frontend:" + subNameSpace + suffixCloudUnit;
            jedis.del(frontend.toLowerCase());
        } catch (JedisConnectionException | UnsupportedEncodingException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }
    @Override
    public void createModuleManagerKey(Application application,
                                       String dockerContainerIP, String modulePort,
                                       String cloudunitModuleManagerSuffix, Long instanceNumber) {

        if (logger.isInfoEnabled()) {
            logger.info("parameters : [ " + application.getName()
                    + " - dockerContainerIP : " + dockerContainerIP
                    + " - env.getProperty(redisIP) : " + env.getProperty("redis.ip")
                    + " - cloudunitModuleManagerSuffix : "
                    + cloudunitModuleManagerSuffix + " - instanceNumber : "
                    + instanceNumber);
        }

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();

            String subNameSpace = concatSubNameSpace(application);

            String alias = null;
            if (System.getenv("CU_SUB_DOMAIN") != null) {
                alias = cloudunitModuleManagerSuffix + instanceNumber + "-"
                        + subNameSpace + System.getenv("CU_SUB_DOMAIN") + env.getProperty("suffix.cloudunit.io");
            } else {
                alias = cloudunitModuleManagerSuffix + instanceNumber + "-"
                        + subNameSpace + env.getProperty("suffix.cloudunit.io");
            }
            
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
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
            // Exit for non blocking
            System.exit(1);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }
    @Override
    public void updatedAdminAddress(Application application,
                                    String dockerManagerIP, String modulePort,
                                    String cloudunitModuleManagerSuffix, Long instanceNumber) {

        if (logger.isInfoEnabled()) {
            logger.info("parameters : [ " + application.getName()
                    + " - dockerManagerIP : " + dockerManagerIP
                    + " - env.getProperty(REDISIP) : " + env.getProperty("redis.ip")
                    + " - cloudunitModuleManagerSuffix : "
                    + cloudunitModuleManagerSuffix + " - instanceNumber : "
                    + instanceNumber);
        }

        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();

            String subNameSpace = concatSubNameSpace(application);
            String alias = cloudunitModuleManagerSuffix + instanceNumber + "-"
                    + subNameSpace + env.getProperty("suffix.cloudunit.io");
            String frontend = "frontend:" + alias;
            String valeur = "http://" + dockerManagerIP + ":" + modulePort;
            if (logger.isInfoEnabled()) {
                logger.info("Mise à jour dans Redis de [" + frontend + "] --> "
                        + "[" + valeur + "]");
            }
            jedis.lset(frontend.toLowerCase(), 1, valeur);
        } catch (JedisConnectionException | UnsupportedEncodingException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }
    @Override
    public void removePhpMyAdminKey(Application application,
                                    String cloudunitModuleManagerSuffix, Long instanceNumber) {

        JedisPool pool = null;
        Jedis jedis = null;

        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();

            String subNameSpace = concatSubNameSpace(application);
            String alias = cloudunitModuleManagerSuffix + instanceNumber + "-"
                    + subNameSpace + env.getProperty("suffix.cloudunit.io");
            String frontend = "frontend:" + alias.toLowerCase();
            jedis.del(frontend);
            if (logger.isInfoEnabled()) {
                logger.info("Suppression dans Redis de [" + frontend + "]");
            }
        } catch (JedisConnectionException | UnsupportedEncodingException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (null != jedis) {
                pool.returnResource(jedis);
                pool.destroy();
            }
        }
    }

    /**
     * Delete an alias into Redis for Hipache
     *
     * @param alias
     */
    @Override
    public void removeAlias(String alias) {

        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = new JedisPool(new JedisPoolConfig(),
                    ApplicationEntryPoint.IP_REDIS, Integer.parseInt(env.getProperty("redis.port")), 3000);
            jedis = pool.getResource();
            String frontend = "frontend:" + alias.toLowerCase() + env.getProperty("suffix.cloudunit.io");
            jedis.del(frontend);
            if (logger.isInfoEnabled()) {
                logger.info("Suppression dans Redis de [" + frontend + "]");
            }
        } catch (JedisConnectionException e) {
            logger.error("HipacheRedisUtils Exception", e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
            }
        } finally {
            if (jedis != null) {
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

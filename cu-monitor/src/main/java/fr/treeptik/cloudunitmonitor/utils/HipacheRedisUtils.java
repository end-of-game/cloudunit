package fr.treeptik.cloudunitmonitor.utils;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import fr.treeptik.cloudunitmonitor.model.Application;

@Component
public class HipacheRedisUtils
{

    @Inject
    private Environment env;

    private Logger logger = Logger.getLogger( HipacheRedisUtils.class );

    public void updateServerAddress( Application application, String redisIp, String dockerManagerIP,
                                     String server8080Port, String serverManagerPort )
    {
        int redisPort = Integer.parseInt( env.getProperty( "redis.port" ) );
        String suffixCloudUnit = application.getSuffixCloudUnitIO();

        JedisPool pool = null;
        Jedis jedis = null;

        try
        {
            pool = new JedisPool( new JedisPoolConfig(), redisIp, redisPort, 3000 );
            jedis = pool.getResource();

            String subNameSpace = concatSubNameSpace( application );

            String key = subNameSpace + suffixCloudUnit;
            String frontend = "frontend:" + key.toLowerCase();
            jedis.lset( frontend, 1, "http://" + dockerManagerIP + ":" + server8080Port );
            /*
             * UPDATE THE ENTRY FOR SERVER MANAGER
             */

            String frontendServerManager = "frontend:manager-" + key.toLowerCase();
            jedis.lset( frontendServerManager, 1, "http://" + dockerManagerIP + ":" + serverManagerPort );
        }
        catch ( JedisConnectionException | UnsupportedEncodingException e )
        {
            logger.error( "HipacheRedisUtils Exception", e );

        }
        finally
        {
            if ( null != jedis )
            {
                pool.returnResource( jedis );
                pool.destroy();
            }
        }
    }

    public void createModuleManagerKey( Application application, String redisIp, String dockerManagerIP,
                                        String module80Port, String cloudunitModuleManagerSuffix, Long instanceNumber )
    {

        if ( logger.isInfoEnabled() )
        {
            logger.info( "parameters : [ " + application.getName() + " - dockerManagerIP : " + dockerManagerIP
                + " - redisIp : " + redisIp + " - cloudunitModuleManagerSuffix : " + cloudunitModuleManagerSuffix
                + " - instanceNumber : " + instanceNumber );
        }

        int redisPort = Integer.parseInt( env.getProperty( "redis.port" ) );

        JedisPool pool = null;
        Jedis jedis = null;
        try
        {
            pool = new JedisPool( new JedisPoolConfig(), redisIp, redisPort, 3000 );
            jedis = pool.getResource();

            String subNameSpace = concatSubNameSpace( application );

            String alias =
                cloudunitModuleManagerSuffix + instanceNumber + "-" + subNameSpace + application.getSuffixCloudUnitIO();
            String frontend = "frontend:" + alias;
            String valeur = "http://" + dockerManagerIP + ":" + module80Port;
            if ( logger.isInfoEnabled() )
            {
                logger.info( "Ajout dans Redis de [" + frontend + "] --> " + "[" + valeur + "]" );
            }
            jedis.rpush( frontend.toLowerCase(), alias.toLowerCase() );
            jedis.rpush( frontend.toLowerCase(), valeur.toLowerCase() );
        }
        catch ( JedisConnectionException | UnsupportedEncodingException e )
        {
            logger.error( "HipacheRedisUtils Exception", e );

        }
        finally
        {
            if ( null != jedis )
            {
                pool.returnResource( jedis );
                pool.destroy();
            }
        }
    }

    public void updateAdminAddress( Application application, String redisIp, String dockerManagerIP,
                                    String module80Port, String cloudunitModuleManagerSuffix, Long instanceNumber )
    {
        if ( logger.isInfoEnabled() )
        {
            logger.info( "parameters : [ " + application.getName() + " - dockerManagerIP : " + dockerManagerIP
                + " - redisIp : " + redisIp + " - cloudunitModuleManagerSuffix : " + cloudunitModuleManagerSuffix
                + " - instanceNumber : " + instanceNumber );
        }
        int redisPort = Integer.parseInt( env.getProperty( "redis.port" ) );
        JedisPool pool = null;
        Jedis jedis = null;

        try
        {
            pool = new JedisPool( new JedisPoolConfig(), redisIp, redisPort, 3000 );
            jedis = pool.getResource();

            String subNameSpace = concatSubNameSpace( application );
            String alias =
                cloudunitModuleManagerSuffix + instanceNumber + "-" + subNameSpace + application.getSuffixCloudUnitIO();
            String frontend = "frontend:" + alias;
            String valeur = "http://" + dockerManagerIP + ":" + module80Port;
            if ( logger.isInfoEnabled() )
            {
                logger.info( "Mise à jour dans Redis de [" + frontend + "] --> " + "[" + valeur + "]" );
            }
            jedis.lset( frontend.toLowerCase(), 1, valeur );
        }
        catch ( JedisConnectionException | UnsupportedEncodingException e )
        {
            logger.error( "HipacheRedisUtils Exception", e );

        }
        finally
        {
            if ( null != jedis )
            {
                pool.returnResource( jedis );
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
    private String concatSubNameSpace( Application application )
        throws UnsupportedEncodingException
    {
        String subNameSpace =
            AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics( application.getName() ) + "-"
                + AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics( application.getUser().getLogin() ) + "-"
                + AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics( application.getUser().getOrganization() );

        return subNameSpace;

    }

}

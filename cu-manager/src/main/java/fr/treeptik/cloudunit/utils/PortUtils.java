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

import fr.treeptik.cloudunit.dao.ProxySshPortDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.ProxySshPort;
import fr.treeptik.cloudunit.service.impl.ApplicationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class PortUtils
{

    public ConcurrentLinkedQueue<Integer> forbbidenPorts = new ConcurrentLinkedQueue<Integer>();

    private Logger logger = LoggerFactory
                    .getLogger( ApplicationServiceImpl.class );

    @Inject
    private ProxySshPortDAO proxySshPortDAO;

    /**
     * Find the next free Ssh and Http Proxy Port values and assign to
     * application git ssh and http proxy port property and set used in table
     * Proxy(Ssh or Http)
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW )
    public Map<String, String> assignProxyPorts( Application application )
                    throws ServiceException
    {

        logger.debug( "-- assignProxyPorts --" );

        Map<String, String> mapProxyPorts = new HashMap<String, String>();

        String freeProxySshPortNumber = proxySshPortDAO.findMinFreePortNumber();
        // Gestion du ProxyPort (retrait du pool de ports libres)
        ProxySshPort proxySshPort = proxySshPortDAO
                        .findByPortNumber( freeProxySshPortNumber );
        proxySshPort.setUsed( true );

        logger.info( "proxySshPort : " + freeProxySshPortNumber );

        // remplit la map pour transférer les valeurs à la méthode appelante
        mapProxyPorts.put( "freeProxySshPortNumber", freeProxySshPortNumber );

        proxySshPort = proxySshPortDAO.save( proxySshPort );

        return mapProxyPorts;
    }

    @Transactional
    public void releaseProxyPorts( Application application )
    {

        ProxySshPort proxySshPort = proxySshPortDAO
                        .findByPortNumber( application.getGitSshProxyPort() );
        proxySshPort.setUsed( false );

        proxySshPort = proxySshPortDAO.save( proxySshPort );

    }

    public Integer getARandomHostPorts( String hostIp )
    {
        int port = randPort( 2599, 2900 );

        // on supprime tous les ports ouverts de forbiddenPorts car ils ne sont
        // plus succeptibles de déclencher une erreur
        if ( !forbbidenPorts.isEmpty() )
        {
            forbbidenPorts.stream().filter( t -> isPortOpened( hostIp, t ) )
                          .forEach( t -> forbbidenPorts.remove( t ) );
        }

        if ( isPortOpened( hostIp, port ) | forbbidenPorts.contains( port ) )
        {
            port = getARandomHostPorts( hostIp );
        }
        // on ajoute le port à la liste des ports à ne pas ouvrir tant que
        // l'opération n'est pas terminée
        forbbidenPorts.add( port );

        return port;
    }

    private boolean isPortOpened( String ip, Integer port )
    {
        try
        {
            Socket socket = new Socket();
            socket.connect( new InetSocketAddress( ip, port ), 500 );
            socket.close();
            return true;
        }
        catch ( Exception ex )
        {
            return false;
        }
    }

    private int randPort( int min, int max )
    {
        Random rand = new Random();
        int randomNum = rand.nextInt( ( max - min ) + 1 ) + min;
        return randomNum;
    }

}

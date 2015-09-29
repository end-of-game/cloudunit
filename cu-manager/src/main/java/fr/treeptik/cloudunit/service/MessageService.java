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

package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

public interface MessageService
{

    @Caching( evict = {
                    @CacheEvict( value = "messageFindCache", key = "#message.author.login" ),
                    @CacheEvict( value = "messageFindCache", key = "{#message.author.login, #message.applicationName}" )
    } )
    Message create( Message message )
                    throws ServiceException;

    @CacheEvict( value = "messageFindCache", key = "#message.author.login" )
    void delete( Message message )
                    throws ServiceException;

    @Cacheable( value = "messageFindCache", key = "#user.login" )
    List<Message> listByUser( User user, int index )
                    throws ServiceException;

    @Cacheable( value = "messageFindCache", key = "{#user.login, #applicationName}" )
    List<Message> listByApp( User user, String applicationName, int index )
                    throws ServiceException;

}

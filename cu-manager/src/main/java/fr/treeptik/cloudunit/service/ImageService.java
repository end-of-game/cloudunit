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
import fr.treeptik.cloudunit.model.Image;

import java.util.List;

public interface ImageService
{

    Image update( Image image )
                    throws ServiceException;

    Image create( Image image )
                    throws ServiceException;

    void remove( Image image )
                    throws ServiceException;

    Image findById( Integer id )
                    throws ServiceException;

    List<Image> findAll()
                    throws ServiceException;

    Image findByName( String name )
                    throws ServiceException;

    Image enableImage( String imageName )
                    throws ServiceException;

    Image disableImage( String imageName )
                    throws ServiceException;

    List<Image> findEnabledImages()
                    throws ServiceException;

    List<Image> findEnabledImagesByType( String type )
                    throws ServiceException;

    Long countNumberOfInstances( String moduleName, String applicationName,
                                 String userLogin )
                    throws ServiceException;

}

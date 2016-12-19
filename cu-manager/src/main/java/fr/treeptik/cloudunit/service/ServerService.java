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

package fr.treeptik.cloudunit.service;

import java.util.List;

import com.spotify.docker.client.exceptions.DockerException;

import fr.treeptik.cloudunit.dto.VolumeAssociationDTO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;

public interface ServerService {

	List<Server> findAll() throws ServiceException;

	Server findById(Integer id) throws ServiceException;

	Server remove(String serverName) throws ServiceException;

	Server update(Server server) throws ServiceException;

	Server startServer(Server server) throws ServiceException;

	Server stopServer(Server server) throws ServiceException;

	Server restartServer(Server server) throws ServiceException;

	Server findByApp(Application application) throws ServiceException;

	Server findByName(String serverName) throws ServiceException;

	Server saveInDB(Server server) throws ServiceException;

	void checkStatus(Server server, String status) throws CheckException;

	boolean checkStatusPENDING(Server server) throws ServiceException;

	Server update(Server server, String memory, String options, boolean restorePreviousEnv)
			throws ServiceException;

	Server findByContainerID(String id) throws ServiceException;

	void changeJavaVersion(Application application, String javaVersion) throws CheckException, ServiceException;

	Server create(Server server) throws ServiceException, CheckException;

	void addVolume(Application application, VolumeAssociationDTO volumeAssociationDTO) throws ServiceException, CheckException;

	void removeVolume(String containerName, String volumeName) throws ServiceException;

}

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

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;

import java.util.List;

public interface ServerService {

	List<Server> findAll() throws ServiceException;

	Server findById(Integer id) throws ServiceException;

	Server remove(String serverName) throws ServiceException;

	Server update(Server server) throws ServiceException;

	Server startServer(Server server) throws ServiceException;

	Server stopServer(Server server) throws ServiceException;

	Server restartServer(Server server) throws ServiceException;

	List<Server> findByApp(Application application) throws ServiceException;

	Server findByName(String serverName) throws ServiceException;

	void checkMaxNumberReach(Application application) throws ServiceException,
			CheckException;

	Server saveInDB(Server server) throws ServiceException;

	List<Server> findAllStatusStopServers() throws ServiceException;

	List<Server> findAllStatusStartServers() throws ServiceException;

	void checkStatus(Server server, String status) throws CheckException;

	boolean checkStatusPENDING(Server server) throws ServiceException;

	Server update(Server server, String memory, String options, String release, boolean restorePreviousEnv) throws ServiceException;

	Server findByContainerID(String id) throws ServiceException;

	Server confirmSSHDStart(String applicationName, String userLogin)
			throws ServiceException;

	void changeJavaVersion(Application application, String javaVersion)
			throws CheckException, ServiceException;

	Server create(Server server, String tag) throws ServiceException,
			CheckException;

	void openPort(String applicationName, String port, String alias,
			boolean isRunning) throws ServiceException;

	void closePort(String applicationName, String port, String alias,
			boolean isRunning) throws ServiceException;
}

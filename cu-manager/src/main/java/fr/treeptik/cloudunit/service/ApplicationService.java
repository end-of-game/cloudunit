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

import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;

import java.io.File;
import java.util.List;

public interface ApplicationService {

	Application findByNameAndUser(User user, String applicationName) throws ServiceException, CheckException;

	List<Application> findAll() throws ServiceException;

	List<Application> findAllByUser(User user) throws ServiceException;

	Long countApp(User user) throws ServiceException;

	void isValid(String applicationName, String serverName) throws ServiceException, CheckException;

	void checkCreate(Application application, String serverName) throws CheckException, ServiceException;

	Application saveInDB(Application application) throws ServiceException;

	boolean checkAppExist(User user, String applicationName) throws ServiceException, CheckException;

	void setStatus(Application application, Status status) throws ServiceException;

	Application deploy(File file, Application application) throws ServiceException, CheckException;

	Application start(Application application) throws ServiceException;

	Application stop(Application application) throws ServiceException;

	List<ContainerUnit> listContainers(String applicationName) throws ServiceException;

	List<String> getListAliases(Application application) throws ServiceException;

	void addNewAlias(Application application, String alias) throws ServiceException, CheckException;

	void updateAliases(Application application) throws ServiceException;

	void removeAlias(Application application, String alias) throws ServiceException, CheckException;

	Application remove(Application application, User user) throws ServiceException, CheckException;

	Application create(String applicationName, String login, String serverName, String tagName, String origin)
			throws ServiceException, CheckException;

	void addPort(Application application, String nature, Integer port) throws ServiceException;

	void removePort(Application application, Integer port) throws CheckException, ServiceException;

	Integer countApplicationsForImage(String cuInstanceName, User user, String tag)
			throws CheckException, ServiceException;

	boolean isStarted(String name);

}

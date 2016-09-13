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

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;

public interface ModuleService {

	List<Module> findAll() throws ServiceException;

	Module findById(Integer id) throws ServiceException;

	Module findByName(String moduleName) throws ServiceException;

	Module update(Module module) throws ServiceException;

	List<Module> findByAppAndUser(User user, String applicationName) throws ServiceException;

	void checkImageExist(String moduleName) throws ServiceException;

	List<Module> findAllStatusStopModules() throws ServiceException;

	List<Module> findAllStatusStartModules() throws ServiceException;

	List<Module> findByApp(Application application) throws ServiceException;

	Module findByContainerID(String id) throws ServiceException;

	void remove(User user, String moduleName, Boolean isModuleRemoving, Status previousApplicationStatus)
			throws ServiceException, CheckException;

	Module stopModule(String moduleName) throws ServiceException;

	Module startModule(String moduleName) throws ServiceException;

	Module publishPort(Integer id, Boolean publishPort, User user)
			throws ServiceException, CheckException;

	Module create(String imageName, Application application, User user) throws ServiceException, CheckException;

}

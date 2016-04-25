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

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;

import java.io.File;
import java.util.List;

public interface ModuleService {

    List<Module> findAll()
        throws ServiceException;

    Module findById(Integer id)
        throws ServiceException;

    Module findByName(String moduleName)
        throws ServiceException;

    Module update(Module module)
        throws ServiceException;

    List<Module> findByAppAndUser(User user, String applicationName)
        throws ServiceException;

    void checkImageExist(String moduleName)
        throws ServiceException;

    void initDb(User user, String applicationName, final String moduleName,
                File file)
        throws ServiceException;

    Module saveInDB(Module module)
        throws ServiceException;

    List<Module> findAllStatusStopModules()
        throws ServiceException;

    List<Module> findAllStatusStartModules()
        throws ServiceException;

    List<Module> findByApp(Application application)
        throws ServiceException;

    Module startModule(Module module)
        throws ServiceException;

    Module stopModule(Module module)
        throws ServiceException;

    void checkStatus(Module module, String status)
        throws CheckException,
        ServiceException;

    boolean checkStatusPENDING(Module module)
        throws ServiceException;

    Module findByContainerID(String id)
        throws ServiceException;

    void addModuleManager(Module module, Long instanceNumber)
        throws ServiceException;

    Module remove(Application application, User user, Module module,
                  Boolean isModuleRemoving, Status previousApplicationStatus)
        throws ServiceException, CheckException;

    Module restoreBackup(String moduleName)
        throws ServiceException;

    Module initModule(Application application, Module module, String tag)
        throws ServiceException, CheckException;

}

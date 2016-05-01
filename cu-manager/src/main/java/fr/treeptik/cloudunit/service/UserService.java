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
import fr.treeptik.cloudunit.model.User;

import java.util.List;

public interface UserService {

    List<User> findByEmail(String email)
        throws ServiceException;

    List<User> findAll()
        throws ServiceException;

    User findById(Integer id)
        throws ServiceException;

    void remove(User user)
        throws ServiceException, CheckException;

    User update(User user)
        throws ServiceException;

    User create(User user)
        throws ServiceException, CheckException;

    void activationAccount(User user)
        throws ServiceException;

    void changePassword(User user, String newPassword)
        throws ServiceException;

    User findByLogin(String login)
        throws ServiceException;

    void changeEmail(User user, String newEmail)
        throws ServiceException;

    String sendPassword(User user)
        throws ServiceException;

    void deleteAllUsersMessages(User user)
        throws ServiceException;

    void changeUserRights(String login, String roleValue)
        throws ServiceException, CheckException;

}

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

package fr.treeptik.cloudunit.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.dao.MessageDAO;
import fr.treeptik.cloudunit.dao.RoleDAO;
import fr.treeptik.cloudunit.dao.UserDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Role;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.CustomPasswordEncoder;
import fr.treeptik.cloudunit.utils.ShellUtils;

@Service
@Lazy(true)
public class UserServiceImpl
        implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    private UserDAO userDAO;

    @Inject
    private MessageDAO messageDAO;

    @Inject
    private RoleDAO roleDAO;

    @Inject
    private ShellUtils shellUtils;

    @Inject
    private ApplicationService applicationService;

    public UserDAO getUserDAO() {
        return this.userDAO;
    }

    @Override
    @Transactional
    public User create(User user)
            throws ServiceException, CheckException {
		// Map<String, Object> mapConfigMail = new HashMap<>();

        try {
            // VALIDATION

            if (user.getClearedPassword().length() < 6
                    & user.getClearedPassword().length() > 16) {
                throw new CheckException(
                        "The password must be have between 6 and 16 characters");
            }
            //ENCODING THE PASSWORD

            user.setPassword(new CustomPasswordEncoder()
                    .encode(user.getClearedPassword()));

            if (user.getEmail() == null || user.getLogin() == null
                    || user.getPassword() == null) {
                throw new CheckException("One of the required is not set");
            }

            if (user.getLogin().length() > 20) {
                throw new CheckException(
                        "The password must be have between 6 and 16 characters");
            }

            if (user.getPassword().length() < 6
                    & user.getPassword().length() > 16) {
                throw new CheckException(
                        "The password must be have between 6 and 16 characters");
            }

            if (!Pattern
                    .compile(
                            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
                    .matcher(user.getEmail()).matches()) {
                throw new CheckException(
                        "This email is incorrect. Please verify.");
            }

            if (this.findByLogin(user.getLogin()) != null) {
                throw new CheckException("This login is already used");
            }
            if (!this.findByEmail(user.getEmail()).isEmpty()) {
                throw new CheckException(
                        "There is already a account registered with this email");
            }
            Role role = new Role();
            role.setDescription("ROLE_USER");
            role = roleDAO.findByRole(role.getDescription());
            user.setRole(role);
            user.setStatus(User.STATUS_MAIL_NOT_CONFIRMED);
            user.setSignin(new Date());

            user = userDAO.save(user);

            // mapConfigMail.put("user", user);
            // mapConfigMail.put("emailType", "activation");
            // emailUtils.sendEmail(mapConfigMail);

        } catch (PersistenceException e) {
            logger.error("UserService Error : Create User" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        logger.info("UserService : User " + user.getLastName()
                + " successfully created.");

        return user;
    }

    @Override
    @Transactional
    public void activationAccount(User user)
            throws ServiceException {
        try {
            logger.debug("UserService : User " + user.toString());

            user = userDAO.findOne(user.getId());
            user.setStatus(User.STATUS_ACTIF);
            user = userDAO.saveAndFlush(user);

        } catch (PersistenceException e) {
            logger.error("UserService Error : Activate User Account" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

        logger.info("UserService : User " + user.getLastName()
                + " account activated - status = " + user.getStatus());

    }

    @Override
    @Transactional
    public User update(User user)
            throws ServiceException {

        logger.debug("update : Methods parameters : " + user.toString());
        logger.info("UserService : Starting updating user "
                + user.getLastName());
        try {
            userDAO.saveAndFlush(user);
        } catch (PersistenceException e) {
            logger.error("UserService Error : update User" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

        logger.info("UserService : User " + user.getLogin()
                + " successfully updated.");

        return user;
    }

    @Override
    @Transactional
    public void remove(User user)
            throws ServiceException, CheckException {
        try {
            logger.debug("remove : Methods parameters : " + user.toString());
            logger.info("Starting removing User " + user.getLastName());

            List<Application> applications = applicationService
                    .findAllByUser(user);

            for (Application application : applications) {
                applicationService.remove(application, user);
            }

            this.deleteAllUsersMessages(user);

            userDAO.delete(user);

            logger.info("UserService : User successfully removed ");

        } catch (PersistenceException e) {

            logger.error("UserService Error : failed to remove "
                    + user.getLastName() + " : " + e);

            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public User findById(Integer id)
            throws ServiceException {
        try {
            logger.debug("findById : Methods parameters : " + id);
            User user = userDAO.findOne(id);
            logger.info("user with id " + id + " found!");
            return user;
        } catch (PersistenceException e) {
            logger.error("Error UserService : error findById Method : " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);

        }
    }

    @Override
    public List<User> findAll()
            throws ServiceException {
        try {
            logger.debug("start findAll");
            List<User> users = userDAO.findAll();
            logger.info("UserService : All Users found ");
            return users;
        } catch (PersistenceException e) {
            logger.error("Error UserService : error findById Method : " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);

        }
    }

    @Override
    public List<User> findByEmail(String email)
            throws ServiceException {
        try {
            logger.debug("Methods parameters : " + email);
            return userDAO.findByEmail(email);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public User findByLogin(String login)
            throws ServiceException {
        try {
            logger.debug("Methods parameters : " + login);
            return userDAO.findByLogin(login);
        } catch (PersistenceException e) {
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Transactional
    @Override
    public void changePassword(User user, String newPassword)
            throws ServiceException {

        Map<String, String> configShell = new HashMap<>();
        String userLogin = user.getLogin();
        user = this.findById(user.getId());
        user.setPassword(newPassword);
        List<Application> listApplications = user.getApplications();

        try {
            logger.debug("Methods parameters : " + user + " new password : "
                    + newPassword);

            userDAO.saveAndFlush(user);
        } catch (PersistenceException e) {
            logger.error("Error UserService : error changePassword : " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
        logger.info("UserService : User " + user.getLastName()
                + " password successfully updated.");

        try {

            for (Application application : listApplications) {
                Server server = application.getServer();
                configShell.put("port", server.getSshPort());
                configShell.put("dockerManagerAddress", server
                        .getApplication().getManagerIp());
                String command = "sh /cloudunit/scripts/change-password.sh "
                        + userLogin + " " + newPassword;
                configShell.put("password", application.getUser()
                        .getPassword());
                shellUtils.executeShell(command, configShell);

                String commandSource = "source /etc/environment";
                logger.debug(commandSource);

                shellUtils.executeShell(commandSource, configShell);
            }

        } catch (Exception e) {
            logger.error("change Passsword - Error execute ssh Request - " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteAllUsersMessages(User user)
            throws ServiceException {
        try {
            messageDAO.deleteAllUsersMessages(user.getId());
        } catch (DataAccessException e) {
            logger.error("Error delete all messages : " + e);
            throw new ServiceException("Error : delete all messages", e);
        }
    }

    @Override
    @Transactional
    public void changeUserRights(String login, String roleValue)
            throws ServiceException, CheckException {

        roleValue = roleValue.toUpperCase();

        if (!(roleValue.equals("USER") || roleValue.equals("ADMIN")))
            throw new CheckException("This role is not available");

        try {
            User user = findByLogin(login);

            if (user.getRole().getDescription().substring(5)
                    .equalsIgnoreCase(roleValue)) {
                throw new CheckException("This user is already an " + roleValue);

            }
            Role role = new Role();
            role.setDescription("ROLE_" + roleValue.toUpperCase());
            role = roleDAO.findByRole(role.getDescription());
            user.setRole(role);
            user = update(user);
        } catch (ServiceException e) {
            throw new ServiceException("Error : change UserRights", e);
        }
    }

}

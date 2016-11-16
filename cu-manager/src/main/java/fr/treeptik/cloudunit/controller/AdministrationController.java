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

package fr.treeptik.cloudunit.controller;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonInputForAdmin;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ImageService;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

/**
 * This controller is restricted to the role ADMIN Security access is defined
 * into SecuryConfiguration
 */
@Controller
@RequestMapping("/admin")
public class AdministrationController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ImageService imageService;

	@Inject
	private UserService userService;

	@Inject
	private MessageService messageService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	/**
	 * Create a new user
	 *
	 * @param input
	 *            : {login:johndoe; firstName:john; lastName:doe;
	 *            email:johndoe@gmail.com; password:xxx}
	 * @return JsonResponse
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@ResponseBody
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public JsonResponse createUser(@RequestBody JsonInputForAdmin input) throws ServiceException, CheckException {

		User user = new User(input.getLogin(), input.getFirstName(), input.getLastName(), input.getOrganization(),
				input.getEmail(), input.getPassword());

		// create a new user
		user = this.userService.create(user);

		this.userService.activationAccount(user);

		return new HttpOk();
	}

	/**
	 * Remove an user
	 *
	 * @param login
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@ResponseBody
	@RequestMapping(value = "/user/{login}", method = RequestMethod.DELETE)
	public JsonResponse removeUser(@PathVariable String login) throws ServiceException, CheckException {
		User user = this.userService.findByLogin(login);
		String contextLogin = this.authentificationUtils.getAuthentificatedUser().getLogin();
		if (login.equalsIgnoreCase(contextLogin)) {
			throw new CheckException("You can't delete your own account from this interface");
		}
		this.userService.remove(user);

		return new HttpOk();
	}

	/**
	 * Retrieve the user with its name
	 *
	 * @param login
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@ResponseBody
	@RequestMapping(value = "/user/{login}", method = RequestMethod.GET)
	public User findByLogin(@PathVariable String login) throws ServiceException, CheckException {
		return this.userService.findByLogin(login);
	}

	/**
	 * List all users
	 *
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@ResponseBody
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<User> findAll() throws ServiceException, CheckException {
		return this.userService.findAll();
	}

	/**
	 * Change the rights for an user
	 *
	 * @param input
	 *            {login:johndoe;role:USER}
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/user/rights", method = RequestMethod.POST)
	public JsonResponse changeRights(@RequestBody JsonInputForAdmin input) throws ServiceException, CheckException {
		String login = this.authentificationUtils.getAuthentificatedUser().getLogin();
		if (login.equalsIgnoreCase(input.getLogin())) {
			throw new CheckException("You can't change your own rights");
		}
		this.userService.changeUserRights(input.getLogin(), input.getRole());
		return new HttpOk();
	}

	/**
	 * Activate an image
	 *
	 * @param imageName
	 * @return
	 * @throws ServiceException
	 */
	@ResponseBody
	@RequestMapping(value = "/images/imageName/{imageName}/enable", method = RequestMethod.POST)
	public Image enableImage(@PathVariable String imageName) throws ServiceException {
		return this.imageService.enableImage(imageName);
	}

	/**
	 * disable an image
	 *
	 * @param imageName
	 * @return
	 * @throws ServiceException
	 */
	@ResponseBody
	@RequestMapping(value = "/images/imageName/{imageName}/disable", method = RequestMethod.POST)
	public Image disableImage(@PathVariable String imageName) throws ServiceException {
		return this.imageService.disableImage(imageName);
	}

	/**
	 * List all actions for an user. Needed for admin panel
	 *
	 * @param login
	 * @param rows
	 * @return
	 * @throws NumberFormatException
	 * @throws ServiceException
	 */
	@ResponseBody
	@RequestMapping(value = "/messages/rows/{rows}/login/{login}", method = RequestMethod.GET)
	public List<Message> listMessages(@PathVariable String login, @PathVariable String rows)
			throws NumberFormatException, ServiceException {
		return messageService.listByUser(userService.findByLogin(login), Integer.parseInt(rows));
	}

}

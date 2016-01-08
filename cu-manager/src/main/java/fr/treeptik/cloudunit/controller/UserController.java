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

import fr.treeptik.cloudunit.dto.HttpErrorServer;
import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonInputForAdmin;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

@Controller
@RequestMapping("/user")
public class UserController
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Inject
    private UserService userService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Value("${cloudunit.instance.name}")
    private String cuInstanceName;

    /**
     * Create an User account and send Email to activate it
     *
     * @param
     * @return
     * @throws CheckException
     * @throws ServiceException
     * @throws UnsupportedEncodingException
     */
    @ResponseBody
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public JsonResponse signin(@RequestBody JsonInputForAdmin input) {
        try {
            User user = new User();
            user.setLogin(input.getLogin());
            user.setEmail(input.getEmail());
            user.setLastName(input.getLastName());
            user.setFirstName(input.getFirstName());
            user.setPassword(input.getPassword());
            user.setOrganization(input.getOrganization());
            logger.debug("UserController - User : " + user);
            userService.create(user);
        } catch (ServiceException | CheckException e) {
            return new HttpErrorServer(e.getMessage());
        }
        logger.info("Signin successfull");
        JsonResponse response = new HttpOk();

        return response;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public
    @ResponseBody
    JsonResponse updateUser(
            @RequestBody JsonInputForAdmin input) {

        User user;
        try {
            user = userService.findByLogin(input.getLogin());
            user.setEmail(input.getEmail());
            user.setLastName(input.getLastName());
            user.setFirstName(input.getFirstName());
            user.setOrganization(input.getOrganization());
            user = userService.update(user);
        } catch (ServiceException e) {
            return new HttpErrorServer(e.getMessage());
        }

        return new HttpOk();
    }

    @RequestMapping(value = "/send-password", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse sendPassword(
            @RequestBody JsonInputForAdmin input) {
        logger.info("--CALL SEND PASSWORD--");
        User user = null;
        try {
            user = userService.findByLogin(input.getLogin());
            userService.sendPassword(user);
        } catch (ServiceException e) {
            return new HttpErrorServer(e.getMessage());
        }
        return new HttpOk();
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.PUT)
    public
    @ResponseBody
    JsonResponse changePassword(
            @RequestBody JsonInputForAdmin input)
            throws CheckException {
        User user = null;
        try {
            logger.info(input.getPassword());
            logger.info(input.getNewPassword());

            user = authentificationUtils.getAuthentificatedUser();

            if (user.getLogin().isEmpty()) {
                throw new CheckException(
                        "This functionnality is not available yet");
            }

            if (!user.getPassword().equalsIgnoreCase(input.getPassword())) {
                throw new CheckException(
                        "Your current password is not correct. Please retry!");
            }

            user.setPassword(input.getNewPassword());

            userService.update(user);

        } catch (ServiceException e) {
            return new HttpErrorServer(e.getMessage());
        }
        return new HttpOk();
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public
    @ResponseBody
    User getStatus()
            throws ServiceException {
        return authentificationUtils.getAuthentificatedUser();
    }

    /**
     * Activate an User account
     *
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @RequestMapping(value = "/activate/login/{login}", method = RequestMethod.GET)
    public
    @ResponseBody
    ModelAndView activationAccount(
            @PathVariable String login)
            throws ServiceException, CheckException {
        logger.info("--ACTIVATION OF ACCOUNT--");
        logger.debug("UserController : User " + login);

        User user = userService.findByLogin(login);

        userService.activationAccount(user);

        logger.info("Activation successfull of account of " + login);
        return new ModelAndView("redirect:/webui/#validated");
    }

    /**
     * Pass id_rsa.pub in json property with "rsa_pub_key" as key.
     *
     * @param userEmail
     * @param rsa_pub_key
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @RequestMapping(value = "/userEmail/{userEmail}/authentificationGit", method = RequestMethod.POST)
    public
    @ResponseBody
    String authentificationGit(
            @PathVariable String userEmail, @RequestBody String rsa_pub_key)
            throws ServiceException, CheckException {

        logger.info("--GIT AUTHENTIFICATION --");
        logger.debug("UserController - User " + userEmail + " rsa_pub_key : "
                + rsa_pub_key);

        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(rsa_pub_key);
        } catch (ParseException e) {
            logger.error("Git Authentification - problem to parse Json - " + e);
            e.printStackTrace();
        }
        userEmail = this.replaceByArobase(userEmail);
        JSONObject jsonObject = (JSONObject) obj;
        rsa_pub_key = (String) jsonObject.get("rsa_pub_key");
        User user = userService.findByEmail(userEmail).get(0);
        userService.authentificationGit(user, rsa_pub_key);

        return "You are authentified on cloudunit git repository";
    }

    /**
     * Get CloudUnit instance name.
     *
     * @return a Map with the cloudunit instance name
     */
    @RequestMapping(value = "/get-cloudunit-instance", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, String> getCloudUnitInstance() {
        Map<String, String> map =  new HashMap<String, String>();
        map.put("cuInstanceName", cuInstanceName);
        return map;
    }

    private String replaceByArobase(String sample) {
        sample = sample.replace("%40", "@");
        return sample;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}

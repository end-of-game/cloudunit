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

import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.UserService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.Serializable;

@Controller
@RequestMapping("/nopublic")
public class NoPublicController
    implements Serializable {

    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(NoPublicController.class);

    @Inject
    private ApplicationService applicationService;

    @Inject
    private UserService userService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @RequestMapping(value = "/git/push", method = RequestMethod.POST)
    public
    @ResponseBody
    JsonResponse saveGitPush(
        @RequestParam String applicationName, @RequestParam String userLogin)
        throws ServiceException, CheckException {
        logger.info("--CALL SAVE GIT PUSH");
        User user = userService.findByLogin(userLogin);
        Application application = applicationService.findByNameAndUser(user, applicationName);
        applicationService.saveGitPush(application, userLogin);
        return new HttpOk();
    }

}
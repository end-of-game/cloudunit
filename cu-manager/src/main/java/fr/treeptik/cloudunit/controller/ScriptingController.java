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

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.dto.*;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.factory.EnvUnitFactory;
import fr.treeptik.cloudunit.manager.ApplicationManager;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.GitlabService;
import fr.treeptik.cloudunit.service.JenkinsService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.CheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Controler for Script execution coming from CLI Syntax
 */
@Controller
@RequestMapping("/scripting")
public class ScriptingController
        implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(ScriptingController.class);

    @Inject
    private DockerService dockerService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @RequestMapping(value = "/execute",
            method = RequestMethod.POST)
    public JsonResponse scriptingExecute(
            @RequestBody ScriptRequestBody scriptRequestBody,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException, InterruptedException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            authentificationUtils.forbidUser(user);

            Random random = new Random();
            int alea = random.nextInt(2);
            Thread.sleep(5000);
            if (alea % 2 ==0) {
                return new HttpErrorServer("Error !");
            }

            // We must be sure there is no running action before starting new one
            this.authentificationUtils.canStartNewAction(null, null, Locale.ENGLISH);

            if (logger.isDebugEnabled()) {
                logger.debug("scriptRequestBody: " + scriptRequestBody);
            }
        } catch (Exception e) {
            logger.error(scriptRequestBody.toString(), e);
        } finally {
            authentificationUtils.allowUser(user);
        }
        return new HttpOk();
    }
}

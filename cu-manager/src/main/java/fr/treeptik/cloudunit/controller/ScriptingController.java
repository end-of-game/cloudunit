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

import fr.treeptik.cloudunit.dto.*;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.ScriptingService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
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
    private ScriptingService scriptingService;

    @Inject
    private DockerService dockerService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @RequestMapping(value = "/script",
            method = RequestMethod.POST)
    public JsonResponse scriptingExecute(
            @RequestBody ScriptRequestBody scriptRequestBody,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException, InterruptedException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            authentificationUtils.forbidUser(user);

            // We must be sure there is no running action before starting new one
            this.authentificationUtils.canStartNewAction(null, null, Locale.ENGLISH);

            if (logger.isDebugEnabled()) {
                logger.debug("scriptRequestBody: " + scriptRequestBody);
            }

            scriptingService.execute(scriptRequestBody.getFileContent(), user.getLogin(), user.getPassword());

        } catch (Exception e) {
            logger.error(scriptRequestBody.toString(), e);
        } finally {
            authentificationUtils.allowUser(user);
        }
        return new HttpOk();
    }
}

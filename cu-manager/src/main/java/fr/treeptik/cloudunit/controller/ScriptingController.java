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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.treeptik.cloudunit.dto.*;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Script;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.ScriptingService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Controller for Script execution coming from CLI Syntax
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

    @RequestMapping(value = "/{id}/exec",
            method = RequestMethod.GET)
    public JsonResponse scriptingExecute(
            @PathVariable @RequestBody Integer id,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException, CheckException, IOException, InterruptedException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            authentificationUtils.forbidUser(user);

            // We must be sure there is no running action before starting new one
            this.authentificationUtils.canStartNewAction(null, null, Locale.ENGLISH);

            Script script = scriptingService.load(id);

            if (logger.isDebugEnabled()) {
                logger.debug("scriptRequestBody: " + script.getContent());
            }

            scriptingService.execute(script.getContent(), user.getLogin(), user.getPassword());

        } finally {
            authentificationUtils.allowUser(user);
        }
        return new HttpOk();
    }

    @RequestMapping(method = RequestMethod.POST)
    public JsonResponse scriptingSave(
            @RequestBody ScriptRequestBody scriptRequestBody, @RequestBody String title,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Script script = new Script();

            script.setCreationUser(user);
            script.setTitle(title);
            script.setContent(scriptRequestBody.getFileContent());
            script.setCreationDate(Calendar.getInstance().getTime());

            scriptingService.save(script);
        } finally {
            authentificationUtils.allowUser(user);
        }
        return new HttpOk();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JsonResponse scriptingLoad(@PathVariable @RequestBody Integer id,
           HttpServletRequest request, HttpServletResponse response)
           throws ServiceException, JsonProcessingException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Script script = scriptingService.load(id);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("id", script.getId());
            ((ObjectNode) rootNode).put("title", script.getTitle());
            ((ObjectNode) rootNode).put("creation_date", script.getCreationDate().toString());
            ((ObjectNode) rootNode).put("creation_user", script.getCreationUser().getFirstName() + " "
                    + script.getCreationUser().getLastName());
            String jsonString = mapper.writeValueAsString(rootNode);

            return new JsonResponse(200, jsonString, null);
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public JsonResponse scriptingLoadAll(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            List<Script> scripts = scriptingService.loadAllScripts();

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode array = mapper.createArrayNode();
            for(Script script : scripts) {
                JsonNode rootNode = mapper.createObjectNode();
                ((ObjectNode) rootNode).put("id", script.getId());
                ((ObjectNode) rootNode).put("title", script.getTitle());
                ((ObjectNode) rootNode).put("content", script.getContent());
                ((ObjectNode) rootNode).put("creation_date", script.getCreationDate().toString());
                ((ObjectNode) rootNode).put("creation_user", script.getCreationUser().getFirstName() + " "
                        + script.getCreationUser().getLastName());
                array.add(rootNode);
            }

            return new JsonResponse(200, array.asText(), null);
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public JsonResponse scriptingUpdate(@PathVariable @RequestBody Integer id, @RequestBody ScriptRequestBody scriptContent)
            throws ServiceException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Script script = scriptingService.load(id);

            script.setContent(scriptContent.getFileContent());
            scriptingService.save(script);
        } finally {
            authentificationUtils.allowUser(user);
        }
        return new HttpOk();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public JsonResponse scriptingDelete(@PathVariable @RequestBody Integer id,
            HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Script script = scriptingService.load(id);
            scriptingService.delete(script);
        } finally {
            authentificationUtils.allowUser(user);
        }
        return new HttpOk();
    }
}

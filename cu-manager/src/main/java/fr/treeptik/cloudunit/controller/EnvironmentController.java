package fr.treeptik.cloudunit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.treeptik.cloudunit.dto.EnvironmentVariableRequest;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Environment;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by stagiaire on 08/08/16.
 */
@Controller
@RequestMapping("/application")
public class EnvironmentController implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(EnvironmentController.class);

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private EnvironmentService environmentService;

    @Inject
    private ApplicationService applicationService;

    @RequestMapping(value = "/{applicationName}/environmentVariables", method = RequestMethod.GET)
    public @ResponseBody ArrayNode loadAllEnvironmentVariables(@PathVariable String applicationName)
            throws ServiceException, JsonProcessingException, CheckException {
        logger.info("Load");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            List<Environment> environmentList = environmentService.loadAllEnvironnments();

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode array = mapper.createArrayNode();

            for (Environment environment : environmentList) {
                JsonNode rootNode = mapper.createObjectNode();
                ((ObjectNode) rootNode).put("id", environment.getId());
                ((ObjectNode) rootNode).put("key", environment.getKeyEnv());
                ((ObjectNode) rootNode).put("value", environment.getValueEnv());
                array.add(rootNode);
            }

            return array;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/environmentVariables/{id}", method = RequestMethod.GET)
    public @ResponseBody JsonNode loadEnvironmentVariable(@PathVariable String applicationName, @PathVariable int id)
            throws ServiceException, CheckException {
        logger.info("Load");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Environment environment = environmentService.loadEnvironnment(id);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("id", environment.getId());
            ((ObjectNode) rootNode).put("key", environment.getKeyEnv());
            ((ObjectNode) rootNode).put("value", environment.getValueEnv());

            return rootNode;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/environmentVariables", method = RequestMethod.POST)
    public @ResponseBody JsonNode addEnvironmentVariable (@PathVariable String applicationName,
            @RequestBody EnvironmentVariableRequest environmentVariableRequest)
            throws ServiceException, CheckException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Application application = applicationService.findByNameAndUser(user, applicationName);
            Environment environment = new Environment();

            environment.setApplication(application);
            environment.setKeyEnv(environmentVariableRequest.getKey());
            environment.setValueEnv(environmentVariableRequest.getValue());

            environmentService.save(environment);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("id", environment.getId());
            ((ObjectNode) rootNode).put("key", environment.getKeyEnv());
            ((ObjectNode) rootNode).put("value", environment.getValueEnv());

            return rootNode;

        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/environmentVariables", method = RequestMethod.PUT)
    public @ResponseBody JsonNode updateEnvironmentVariable (@PathVariable String applicationName,
                  @RequestBody EnvironmentVariableRequest environmentVariableRequest)
            throws ServiceException, CheckException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Application application = applicationService.findByNameAndUser(user, applicationName);
            Environment environment = new Environment();

            environment.setApplication(application);
            environment.setKeyEnv(environmentVariableRequest.getKey());
            environment.setValueEnv(environmentVariableRequest.getValue());

            environmentService.save(environment);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("id", environment.getId());
            ((ObjectNode) rootNode).put("key", environment.getKeyEnv());
            ((ObjectNode) rootNode).put("value", environment.getValueEnv());

            return rootNode;

        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/environmentVariables/{id}", method = RequestMethod.DELETE)
    public void deleteEnvironmentVariable(@PathVariable String applicationName, @PathVariable int id)
            throws ServiceException, CheckException {
        logger.info("Delete");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Environment environment = environmentService.loadEnvironnment(id);

            if(environment.equals(null)) {
                return ;
            }

            environmentService.delete(id);

        } finally {
            authentificationUtils.allowUser(user);
        }
    }
}

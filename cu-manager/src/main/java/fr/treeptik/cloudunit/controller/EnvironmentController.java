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
import java.util.ArrayList;
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
    public @ResponseBody List<EnvironmentVariableRequest> loadAllEnvironmentVariables(@PathVariable String applicationName)
            throws ServiceException, JsonProcessingException, CheckException {
        logger.info("Load");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            List<Environment> environmentList = environmentService.loadAllEnvironnments();
            List<EnvironmentVariableRequest> environmentVariableRequestList = new ArrayList<>();

            for (Environment environment : environmentList) {
                EnvironmentVariableRequest environmentVariableRequest = new EnvironmentVariableRequest();
                environmentVariableRequest.setId(environment.getId());
                environmentVariableRequest.setKey(environment.getKeyEnv());
                environmentVariableRequest.setValue(environment.getValueEnv());
                environmentVariableRequestList.add(environmentVariableRequest);
            }

            return environmentVariableRequestList;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/environmentVariables/{id}", method = RequestMethod.GET)
    public @ResponseBody EnvironmentVariableRequest loadEnvironmentVariable(@PathVariable String applicationName, @PathVariable int id)
            throws ServiceException, CheckException {
        logger.info("Load");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Environment environment = environmentService.loadEnvironnment(id);
            if(environment.equals(null))
                throw new CheckException("Environment variable doesn't exist");

            EnvironmentVariableRequest environmentVariableRequest = new EnvironmentVariableRequest();
            environmentVariableRequest.setId(environment.getId());
            environmentVariableRequest.setKey(environment.getKeyEnv());
            environmentVariableRequest.setValue(environment.getValueEnv());

            return environmentVariableRequest;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/environmentVariables", method = RequestMethod.POST)
    public @ResponseBody EnvironmentVariableRequest addEnvironmentVariable (@PathVariable String applicationName,
            @RequestBody EnvironmentVariableRequest environmentVariableRequest)
            throws ServiceException, CheckException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            if(!environmentVariableRequest.getKey().matches("^[-a-zA-Z0-9_]*$"))
                throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKey());

            List<Environment> environmentList = environmentService.loadAllEnvironnments();
            for(Environment environment : environmentList)
                if (environment.getKeyEnv().equals(environmentVariableRequest.getKey()))
                    throw new CheckException("This key already exists");

            Application application = applicationService.findByNameAndUser(user, applicationName);
            Environment environment = new Environment();

            environment.setApplication(application);
            environment.setKeyEnv(environmentVariableRequest.getKey());
            environment.setValueEnv(environmentVariableRequest.getValue());

            environmentService.save(environment);

            EnvironmentVariableRequest environmentVariableRequest1 = new EnvironmentVariableRequest();
            List<Environment> environmentList1 = environmentService.loadEnvironnmentsByApplication(applicationName);
            for(Environment environment1 : environmentList1)
                if(environment1.getKeyEnv().equals(environment.getKeyEnv())) {
                    environmentVariableRequest1.setId(environment1.getId());
                    environmentVariableRequest1.setKey(environment1.getKeyEnv());
                    environmentVariableRequest1.setValue(environment1.getValueEnv());
                }
                
            return environmentVariableRequest1;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/environmentVariables/{id}", method = RequestMethod.PUT)
    public @ResponseBody EnvironmentVariableRequest updateEnvironmentVariable (@PathVariable String applicationName, @PathVariable int id,
                  @RequestBody EnvironmentVariableRequest environmentVariableRequest)
            throws ServiceException, CheckException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            if(!environmentVariableRequest.getKey().matches("^[-a-zA-Z0-9_]*$"))
                throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKey());

            Environment environment = environmentService.loadEnvironnment(id);
            List<Environment> environmentList = environmentService.loadAllEnvironnments();
            for(Environment environment1 : environmentList)
                if (environment1.getKeyEnv().equals(environmentVariableRequest.getKey()) && !environment1.getKeyEnv().equals(environment.getKeyEnv()))
                    throw new CheckException("This key already exists");


            if(environment.equals(null))
                throw new CheckException("Environment variable doesn't exist");

            environment.setKeyEnv(environmentVariableRequest.getKey());
            environment.setValueEnv(environmentVariableRequest.getValue());

            environmentService.save(environment);

            EnvironmentVariableRequest returnEnv = new EnvironmentVariableRequest();
            returnEnv.setId(environment.getId());
            returnEnv.setKey(environment.getKeyEnv());
            returnEnv.setValue(environment.getValueEnv());

            return returnEnv;

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
                throw new CheckException("Environment variable doesn't exist");
            }

            environmentService.delete(id);

        } finally {
            authentificationUtils.allowUser(user);
        }
    }
}

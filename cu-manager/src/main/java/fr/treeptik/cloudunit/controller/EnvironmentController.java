package fr.treeptik.cloudunit.controller;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.treeptik.cloudunit.dto.EnvironmentVariableRequest;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/application")
public class EnvironmentController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(EnvironmentController.class);

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private EnvironmentService environmentService;

	@RequestMapping(value = "/{applicationName}/container/{containerName}/environmentVariables", method = RequestMethod.GET)
	public @ResponseBody List<EnvironmentVariableRequest> loadAllEnvironmentVariables(
			@PathVariable String applicationName, @PathVariable String containerName)
			throws ServiceException, JsonProcessingException, CheckException {
		logger.info("Load");
		List<EnvironmentVariableRequest> environmentVariableRequestList = environmentService
				.loadEnvironnmentsByContainer(containerName);
		return environmentVariableRequestList;
	}

	@RequestMapping(value = "/{applicationName}/container/{containerName}/environmentVariables/{id}", method = RequestMethod.GET)
	public @ResponseBody EnvironmentVariableRequest loadEnvironmentVariable(@PathVariable String applicationName,
			@PathVariable String containerName, @PathVariable int id) throws ServiceException, CheckException {
		logger.info("Load");
		EnvironmentVariableRequest environmentVariableRequest = environmentService.loadEnvironnment(id);
		return environmentVariableRequest;
	}

	@RequestMapping(value = "/{applicationName}/container/{containerName}/environmentVariables", method = RequestMethod.POST)
	public @ResponseBody JsonResponse addEnvironmentVariable(@PathVariable String applicationName,
			@PathVariable String containerName, @RequestBody EnvironmentVariableRequest environmentVariableRequest)
			throws ServiceException, CheckException {
		User user = authentificationUtils.getAuthentificatedUser();
		environmentService.save(user, environmentVariableRequest, applicationName, containerName);
		return new HttpOk();

	}

	@RequestMapping(value = "/{applicationName}/container/{containerName}/environmentVariables/{id}", method = RequestMethod.PUT)
	public @ResponseBody JsonResponse updateEnvironmentVariable(@PathVariable String applicationName,
			@PathVariable String containerName, @PathVariable int id,
			@RequestBody EnvironmentVariableRequest environmentVariableRequest)
			throws ServiceException, CheckException {
		User user = authentificationUtils.getAuthentificatedUser();
		environmentService.update(user, environmentVariableRequest, applicationName, containerName, id);
		return new HttpOk();
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/environmentVariables/{id}", method = RequestMethod.DELETE)
	public @ResponseBody JsonResponse deleteEnvironmentVariable(@PathVariable String applicationName,
			@PathVariable String containerName, @PathVariable int id) throws ServiceException, CheckException {
		logger.info("Delete");
		environmentService.delete(id);
		return new HttpOk();

	}
}

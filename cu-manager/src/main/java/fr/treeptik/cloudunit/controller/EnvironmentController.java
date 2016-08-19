package fr.treeptik.cloudunit.controller;

import java.io.Serializable;
import java.util.ArrayList;
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
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Environment;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
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

	@Inject
	private ApplicationService applicationService;

	@RequestMapping(value = "/{applicationName}/container/{containerId}/environmentVariables", method = RequestMethod.GET)
	public @ResponseBody List<EnvironmentVariableRequest> loadAllEnvironmentVariables(
			@PathVariable String applicationName, @PathVariable String containerId)
			throws ServiceException, JsonProcessingException, CheckException {
		logger.info("Load");
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			List<EnvironmentVariableRequest> environmentVariableRequestList = environmentService.loadEnvironnmentsByContainer(containerId);

			return environmentVariableRequestList;
		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/environmentVariables/{id}", method = RequestMethod.GET)
	public @ResponseBody EnvironmentVariableRequest loadEnvironmentVariable(@PathVariable String applicationName,
			@PathVariable String containerId, @PathVariable int id) throws ServiceException, CheckException {
		logger.info("Load");
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			EnvironmentVariableRequest environmentVariableRequest = environmentService.loadEnvironnment(id);

			return environmentVariableRequest;
		} catch (Exception e) {
			throw e;
		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/environmentVariables", method = RequestMethod.POST)
	public @ResponseBody JsonResponse addEnvironmentVariable(@PathVariable String applicationName,
										@PathVariable String containerId, @RequestBody EnvironmentVariableRequest environmentVariableRequest)
			throws ServiceException, CheckException {
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			environmentService.save(user, environmentVariableRequest, applicationName, containerId);

			return new HttpOk();
		} catch (Exception e) {
			throw e;
		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/environmentVariables/{id}", method = RequestMethod.PUT)
	public @ResponseBody JsonResponse updateEnvironmentVariable(@PathVariable String applicationName,
			@PathVariable String containerId, @PathVariable int id,
			@RequestBody EnvironmentVariableRequest environmentVariableRequest)
			throws ServiceException, CheckException {
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			environmentService.update(user, environmentVariableRequest, applicationName, containerId, id);

			return new HttpOk();
		} catch (Exception e) {
			throw e;
		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/environmentVariables/{id}", method = RequestMethod.DELETE)
	public @ResponseBody JsonResponse deleteEnvironmentVariable(@PathVariable String applicationName, @PathVariable String containerId,
			@PathVariable int id) throws ServiceException, CheckException {
		logger.info("Delete");
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			environmentService.delete(id);

			return new HttpOk();
		} catch (Exception e) {
			throw e;
		} finally {
			authentificationUtils.allowUser(user);
		}
	}
}

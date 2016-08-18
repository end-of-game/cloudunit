package fr.treeptik.cloudunit.controller;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.dto.VolumeRequest;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.VolumeService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/application")
public class VolumeController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(VolumeController.class);

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private VolumeService volumeService;

	@Inject
	private ApplicationService applicationService;

	@RequestMapping(value = "/{applicationName}/container/{containerId}/volumes", method = RequestMethod.GET)
	public @ResponseBody List<VolumeRequest> loadAllVolumes(@PathVariable String applicationName,
			@PathVariable String containerId) throws ServiceException, JsonProcessingException, CheckException {
		logger.info("Load");
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			List<Volume> volumes = volumeService.loadVolumeByContainer(containerId);
			return volumes.stream().map(v -> v.mapToVolume()).collect(Collectors.toList());

		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/volumes/{id}", method = RequestMethod.GET)
	public @ResponseBody VolumeRequest loadVolume(@PathVariable String applicationName,
			@PathVariable String containerId, @PathVariable int id) throws ServiceException, CheckException {
		logger.info("Load");
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			return volumeService.loadVolume(id).mapToVolume();
		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/volumes", method = RequestMethod.POST)
	public JsonResponse addVolume(@PathVariable String applicationName, @PathVariable String containerId,
			@RequestBody VolumeRequest volumeRequest) throws ServiceException, CheckException {
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			Application application = applicationService.findByNameAndUser(user, applicationName);
			Volume volume = volumeRequest.mapToVolumeRequest();
			volume.setApplication(application);
			volume.setContainerId(containerId);
			volumeService.save(volume);
			return new HttpOk();
		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/volumes/{id}", method = RequestMethod.PUT)
	public @ResponseBody JsonResponse updateVolume(@PathVariable String applicationName,
			@PathVariable String containerId, @PathVariable int id, @RequestBody VolumeRequest volumeRequest)
			throws ServiceException, CheckException {

		User user = authentificationUtils.getAuthentificatedUser();
		try {
			volumeService.save(volumeRequest.mapToVolumeRequest());
			return new HttpOk();

		} finally {
			authentificationUtils.allowUser(user);
		}
	}

	@RequestMapping(value = "/{applicationName}/container/{containerId}/volumes/{id}", method = RequestMethod.DELETE)
	public void deleteEnvironmentVariable(@PathVariable String applicationName, @PathVariable String containerId,
			@PathVariable int id) throws ServiceException, CheckException {
		logger.info("Delete");
		User user = authentificationUtils.getAuthentificatedUser();
		try {
			volumeService.delete(id);
		} finally {
			authentificationUtils.allowUser(user);
		}
	}
}

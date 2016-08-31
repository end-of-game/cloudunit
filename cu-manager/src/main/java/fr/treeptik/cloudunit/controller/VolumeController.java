package fr.treeptik.cloudunit.controller;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.service.VolumeService;

@Controller
@RequestMapping("/volume")
public class VolumeController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(VolumeController.class);

	@Inject
	private VolumeService volumeService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Volume> loadAllVolumes()
			throws ServiceException, JsonProcessingException, CheckException {
		logger.info("Load");
		List<Volume> volumes = volumeService.loadAllVolumes();
		return volumes;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody Volume loadVolume(@PathVariable int id) throws ServiceException, CheckException {
		return volumeService.loadVolume(id);
	}

	// REQUEST BODY => * Name : String
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody JsonResponse addVolume(@RequestBody Volume volume) throws ServiceException, CheckException {
		volumeService.createNewVolume(volume);
		return new HttpOk();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public @ResponseBody JsonResponse updateVolume(@RequestBody Volume volume) throws ServiceException, CheckException {
		volumeService.updateVolume(volume);
		return new HttpOk();

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteVolume(@PathVariable Integer id) throws ServiceException, CheckException {
		volumeService.delete(id);
	}
}

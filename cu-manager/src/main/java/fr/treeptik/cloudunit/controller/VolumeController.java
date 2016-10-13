package fr.treeptik.cloudunit.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.treeptik.cloudunit.dto.VolumeAssociationResource;
import fr.treeptik.cloudunit.dto.VolumeResource;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.model.VolumeAssociation;
import fr.treeptik.cloudunit.service.VolumeService;

@Controller
@RequestMapping("/volume")
public class VolumeController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(VolumeController.class);

	@Inject
	private VolumeService volumeService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<VolumeResource>> loadAllVolumes()
			throws ServiceException, JsonProcessingException, CheckException {
		logger.info("List all volumes");
		List<Volume> volumes = volumeService.loadAllVolumes();
		List<VolumeResource> volumeResourceList = volumes.stream().map(VolumeResource::new).collect(Collectors.toList());
		return ResponseEntity.status(HttpStatus.OK).body(volumeResourceList);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<VolumeResource> loadVolume(@PathVariable int id) throws ServiceException, CheckException {
		Volume volume = volumeService.loadVolume(id);
		VolumeResource volumeResource = new VolumeResource(volume);
		return ResponseEntity.status(HttpStatus.OK).body(volumeResource);
	}

	@RequestMapping(value = "/{id}/associations", method = RequestMethod.GET)
	public ResponseEntity<?> loadVolumeAssociation(@PathVariable int id) throws ServiceException, CheckException {
		Set<VolumeAssociation> volumeAssociations = volumeService.loadVolumeAssociations(id);
		List<VolumeAssociationResource> resources = volumeAssociations.stream()
				.map(VolumeAssociationResource::new).collect(Collectors.toList());
		return ResponseEntity.ok(resources);
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<VolumeResource> addVolume(@RequestBody VolumeResource request) throws ServiceException, CheckException {
		Volume volume = volumeService.createNewVolume(request.getName());
		VolumeResource volumeResource = new VolumeResource(volume);
		return ResponseEntity.status(HttpStatus.CREATED).body(volumeResource);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<VolumeResource> updateVolume(@RequestBody Volume request) throws ServiceException, CheckException {
		Volume volume = volumeService.updateVolume(request);
		VolumeResource volumeResource = new VolumeResource(volume);
		return ResponseEntity.status(HttpStatus.OK).body(volumeResource);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteVolume(@PathVariable Integer id) throws ServiceException, CheckException {
		volumeService.delete(id);
	}
}

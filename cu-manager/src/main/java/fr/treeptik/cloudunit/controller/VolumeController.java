package fr.treeptik.cloudunit.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.treeptik.cloudunit.dto.EnvironmentVariableRequest;
import fr.treeptik.cloudunit.dto.VolumeRequest;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Environment;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.VolumeService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/application")
public class VolumeController implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(VolumeController.class);

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private VolumeService volumeService;

    @Inject
    private ApplicationService applicationService;

    @RequestMapping(value = "/{applicationName}/container/{containerId}/volumes", method = RequestMethod.GET)
    public @ResponseBody
    List<VolumeRequest> loadAllVolumes(@PathVariable String applicationName,
            @PathVariable String containerId)
            throws ServiceException, JsonProcessingException, CheckException {
        logger.info("Load");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            List<Volume> volumeList = volumeService.loadVolumeByContainer(containerId);
            List<VolumeRequest> volumeRequests = new ArrayList<>();

            for (Volume volume : volumeList) {
                VolumeRequest volumeRequest = new VolumeRequest();
                volumeRequest.setId(volume.getId());
                volumeRequest.setName(volume.getName());
                volumeRequest.setPath(volume.getPath());
                volumeRequests.add(volumeRequest);
            }

            return volumeRequests;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/volumes/{id}", method = RequestMethod.GET)
    public @ResponseBody VolumeRequest loadVolume(@PathVariable String applicationName,
            @PathVariable String containerId, @PathVariable int id)
            throws ServiceException, CheckException {
        logger.info("Load");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Volume volume = volumeService.loadVolume(id);
            if(volume.equals(null))
                throw new CheckException("Volume doesn't exist");

            VolumeRequest volumeRequest = new VolumeRequest();
            volumeRequest.setId(volume.getId());
            volumeRequest.setName(volume.getName());
            volumeRequest.setPath(volume.getPath());

            return volumeRequest;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/volumes", method = RequestMethod.POST)
    public @ResponseBody VolumeRequest addVolume (@PathVariable String applicationName,
            @PathVariable String containerId, @RequestBody VolumeRequest volumeRequest)
            throws ServiceException, CheckException {
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            if(volumeRequest.getName() == null || volumeRequest.getName().isEmpty())
                throw new CheckException("This name is not consistent !");

            if(volumeRequest.getPath() == null || volumeRequest.getPath().isEmpty())
                throw new CheckException("This path is not consistent !");

            if(!volumeRequest.getName().matches("^[-a-zA-Z0-9_]*$"))
                throw new CheckException("This name is not consistent : " + volumeRequest.getName());

            if(!volumeRequest.getPath().matches("^[a-zA-Z0-9\\-\\/_]*$"))
                throw new CheckException("This path is not consistent : " + volumeRequest.getPath());

            List<Volume> volumeList = volumeService.loadAllVolumes();
            for(Volume volume : volumeList)
                if (volume.getName().equals(volumeRequest.getName()))
                    throw new CheckException("This name already exists");

            Application application = applicationService.findByNameAndUser(user, applicationName);
            Volume volume = new Volume();

            volume.setApplication(application);
            volume.setContainerId(containerId);
            volume.setName(volumeRequest.getName());
            volume.setPath(volumeRequest.getPath());

            volumeService.save(volume);

            VolumeRequest volumeRequest1 = new VolumeRequest();
            List<Volume> volumeList1 = volumeService.loadVolumeByApplication(applicationName);
            for(Volume volume1 : volumeList1)
                if(volume1.getName().equals(volume.getName())) {
                    volumeRequest1.setId(volume1.getId());
                    volumeRequest1.setName(volume1.getName());
                    volumeRequest1.setPath(volume1.getPath());
                }

            return volumeRequest1;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/volumes/{id}", method = RequestMethod.PUT)
    public @ResponseBody VolumeRequest updateVolume (@PathVariable String applicationName,
           @PathVariable String containerId, @PathVariable int id, @RequestBody VolumeRequest volumeRequest)
           throws ServiceException, CheckException {

        User user = authentificationUtils.getAuthentificatedUser();
        try {
            if(volumeRequest.getName() == null || volumeRequest.getName().isEmpty())
                throw new CheckException("This name is not consistent !");

            if(volumeRequest.getPath() == null || volumeRequest.getPath().isEmpty())
                throw new CheckException("This path is not consistent !");

            if(!volumeRequest.getName().matches("^[-a-zA-Z0-9_]*$"))
                throw new CheckException("This name is not consistent : " + volumeRequest.getName());

            if(!volumeRequest.getPath().matches("^[a-zA-Z0-9\\-\\/_]*$"))
                throw new CheckException("This path is not consistent : " + volumeRequest.getPath());

            Volume volume = volumeService.loadVolume(id);
            List<Volume> volumeList = volumeService.loadAllVolumes();
            for(Volume volume1 : volumeList)
                if (volume1.getName().equals(volumeRequest.getName()) && !volume1.getName().equals(volume.getName()))
                    throw new CheckException("This name already exists");

            if(volume.equals(null))
                throw new CheckException("Volume doesn't exist");

            volume.setName(volumeRequest.getName());
            volume.setPath(volumeRequest.getPath());

            volumeService.save(volume);

            VolumeRequest volumeRequest1 = new VolumeRequest();
            volumeRequest1.setId(volume.getId());
            volumeRequest1.setName(volume.getName());
            volumeRequest1.setPath(volume.getPath());

            return volumeRequest1;

        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/volumes/{id}", method = RequestMethod.DELETE)
    public void deleteEnvironmentVariable(@PathVariable String applicationName,
            @PathVariable String containerId, @PathVariable int id)
            throws ServiceException, CheckException {
        logger.info("Delete");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Volume volume = volumeService.loadVolume(id);

            if(volume.equals(null)) {
                throw new CheckException("Volume doesn't exist");
            }

            volumeService.delete(id);

        } finally {
            authentificationUtils.allowUser(user);
        }
    }
}

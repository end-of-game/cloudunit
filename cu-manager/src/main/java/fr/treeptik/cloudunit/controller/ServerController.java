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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.io.Serializable;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.ServerResource;
import fr.treeptik.cloudunit.dto.VolumeAssociationDTO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/applications/{id}/server")
public class ServerController implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerController.class);

	private final Locale locale = Locale.ENGLISH;

	@Inject
	private ApplicationService applicationService;
	
	@Inject
	private ApplicationDAO applicationDAO;

	@Inject
	private ServerService serverService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private ApplicationEventPublisher applicationEventPublisher;
	
	private ServerResource toResource(Server server) {
	    ServerResource resource = new ServerResource(server);
	    
	    Integer id = server.getApplication().getId();
	    
        try {
            resource.add(linkTo(methodOn(ServerController.class).getServer(id))
                    .withSelfRel());
            resource.add(linkTo(methodOn(ApplicationController.class).detail(id))
                    .withRel("application"));
            resource.add(linkTo(methodOn(ContainerController.class).getContainer(id, server.getContainerID()))
                    .withRel("container"));
        } catch (CheckException | ServiceException e) {
            // ignore
        }
	    
	    return resource;
	}
	
	@GetMapping
	public ResponseEntity<?> getServer(@PathVariable  Integer id) {
	    Application application = applicationDAO.findOne(id);
	    
	    if (application == null || application.getServer() == null) {
	        return ResponseEntity.notFound().build();
	    }
	    
	    Server server = application.getServer();
	    
	    return ResponseEntity.ok(toResource(server));
	}

    @CloudUnitSecurable
    @PutMapping
    public ResponseEntity<?> updateServer(
            @PathVariable Integer id,
            @Validated(ServerResource.Full.class) @RequestBody ServerResource request)
                    throws ServiceException, CheckException {
        Application application = applicationDAO.findOne(id);
        
        User user = authentificationUtils.getAuthentificatedUser();
        authentificationUtils.canStartNewAction(user, application, locale);

        applicationService.setStatus(application, Status.PENDING);

        Server server = application.getServer();
        
        request.put(server);
        try {
            serverService.update(server);
        } finally {
            applicationService.setStatus(application, Status.FAIL);
        }

        applicationService.setStatus(application, Status.START);

        return ResponseEntity.ok(toResource(server));
    }
	
	@CloudUnitSecurable
	@PatchMapping
	public ResponseEntity<?> patchServer(
	        @PathVariable Integer id,
	        @Validated(ServerResource.Patch.class) @RequestBody ServerResource request)
	                throws ServiceException, CheckException {
	    Application application = applicationDAO.findOne(id);
	    
		User user = authentificationUtils.getAuthentificatedUser();
		authentificationUtils.canStartNewAction(user, application, locale);

		applicationService.setStatus(application, Status.PENDING);

		Server server = application.getServer();
		
		request.patch(server);
		try {
			serverService.update(server);
		} finally {
			applicationService.setStatus(application, Status.FAIL);
		}

		applicationService.setStatus(application, Status.START);

		return ResponseEntity.ok(toResource(server));
	}
	
    @CloudUnitSecurable
    @RequestMapping(value = "/volumes", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ResponseEntity<?> linkVolumeAssociation(
            @PathVariable Integer id,
            @RequestBody VolumeAssociationDTO volumeAssociationDTO)
            throws ServiceException, CheckException {
        // TODO extend volume association to all containers (CU-281)
        LOGGER.debug("{}", volumeAssociationDTO);
        
        Application application = applicationDAO.findOne(id);
        
        if (application == null || application.getServer() == null) {
            return ResponseEntity.notFound().build();
        }
        
        serverService.addVolume(application, volumeAssociationDTO);

        applicationEventPublisher.publishEvent(new ServerStartEvent(application.getServer()));
        applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/volumes/{volumeName}/container/{containerName}")
    @ResponseBody
    public ResponseEntity<?> removeVolume(
            @PathVariable Integer id,
            @PathVariable String volumeName) throws ServiceException, CheckException {
        // TODO extend volume association to all containers (CU-281)
        Application application = applicationDAO.findOne(id);
        
        if (application == null || application.getServer() == null) {
            return ResponseEntity.notFound().build();
        }
        
        Server server = application.getServer();
        
        LOGGER.debug("{} {}", server.getName(), volumeName);
        serverService.removeVolume(server.getName(), volumeName);
        return ResponseEntity.noContent().build();
    }

}

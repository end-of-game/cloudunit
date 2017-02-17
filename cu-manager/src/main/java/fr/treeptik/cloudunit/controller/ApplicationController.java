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

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.config.events.ApplicationFailEvent;
import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStopEvent;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.ApplicationResource;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

/**
 * Controller about Application lifecycle Application is the main concept for
 * CloudUnit : it composed by Server, Module and Metadata
 */
@Controller
@RequestMapping("/applications")
public class ApplicationController {
	private final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

	@Inject
	private ApplicationService applicationService;
	
	@Inject
	private ApplicationDAO applicationDAO;

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private ApplicationEventPublisher applicationEventPublisher;
	
	private ApplicationResource toResource(Application application) {
	    ApplicationResource resource = new ApplicationResource(application);
	    
	    try {
            Integer id = application.getId();
            resource.add(linkTo(methodOn(ApplicationController.class).detail(id))
                    .withSelfRel());
			if(application.getStatus() == Status.STOP) {
				resource.add(linkTo(methodOn(ApplicationController.class).startApplication(id))
						.withRel("start"));
			}

			if(application.getStatus() == Status.START) {
				resource.add(linkTo(methodOn(ApplicationController.class).stopApplication(id))
						.withRel("stop"));
				resource.add(linkTo(methodOn(ApplicationController.class).restartApplication(id))
						.withRel("restart"));
			}

				resource.add(linkTo(methodOn(ApplicationController.class).deleteApplication(id))
						.withRel("delete"));
//			resource.add(linkTo(methodOn(ModuleController.class).getModules(id))
//					.withRel("modules"));

        } catch (CheckException | InterruptedException | ServiceException e) {
            // ignore
        }
	    
	    return resource;
	}

	@Transactional
	@PostMapping
	public ResponseEntity<?> createApplication(@Valid @RequestBody ApplicationResource request)
	        throws ServiceException, CheckException, InterruptedException {
		// We must be sure there is no running action before starting new one
		User user = authentificationUtils.getAuthentificatedUser();
		authentificationUtils.canStartNewAction(user, null, Locale.ENGLISH);

		Application result = applicationService.create(request.getName(), request.getServerType());
		
		ApplicationResource resource = toResource(result);
        return ResponseEntity
                .created(URI.create(resource.getId().getHref()))
                .body(resource);
	}

	@CloudUnitSecurable
	@PostMapping("/{id}/restart")
	@Transactional
	public ResponseEntity<?> restartApplication(@PathVariable Integer id)
			throws ServiceException, CheckException, InterruptedException {
		Application application = applicationDAO.findOne(id);

		if (application == null || application.getStatus() != Status.START) {
			return ResponseEntity.notFound().build();
		}

		User user = authentificationUtils.getAuthentificatedUser();
		// We must be sure there is no running action before starting new one
		authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

		applicationService.stop(application);
		applicationService.start(application);

		return ResponseEntity.noContent().build();
	}

	@CloudUnitSecurable
	@PostMapping(value = "/{id}/start")
	@Transactional
	public ResponseEntity<?> startApplication(@PathVariable Integer id)
			throws ServiceException, CheckException, InterruptedException {
		Application application = applicationDAO.findOne(id);

		if (application == null || application.getStatus() != Status.STOP) {
			return ResponseEntity.notFound().build();
		}
        User user = authentificationUtils.getAuthentificatedUser();
		// We must be sure there is no running action before starting new one
		authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

		// set the application in pending mode
		applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));

		applicationService.start(application);

		// wait for modules and servers starting
		applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));

		return ResponseEntity.noContent().build();
	}

	@CloudUnitSecurable
	@PostMapping("/{id}/stop")
	@Transactional
	public ResponseEntity<?> stopApplication(@PathVariable Integer id) throws ServiceException, CheckException {
		Application application = applicationDAO.findOne(id);
		
		if (application == null || application.getStatus() != Status.START) {
		    return ResponseEntity.notFound().build();
		}

        User user = authentificationUtils.getAuthentificatedUser();
		// We must be sure there is no running action before starting new one
		authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

		// set the application in pending mode
		applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));

		// stop the application
		applicationService.stop(application);

		applicationEventPublisher.publishEvent(new ApplicationStopEvent(application));

		return ResponseEntity.noContent().build();
	}

	@CloudUnitSecurable
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteApplication(@PathVariable Integer id) throws ServiceException, CheckException {
		Application application = applicationDAO.findOne(id);

		if (application == null) {
		    return ResponseEntity.notFound().build();
		}
		
		// We must be sure there is no running action before starting new one
        User user = this.authentificationUtils.getAuthentificatedUser();
		authentificationUtils.canStartDeleteApplicationAction(user, application, Locale.ENGLISH);

		try {
			// Application busy
			// set the application in pending mode
			applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));

			logger.info("delete application: {}", application.getName());
			applicationService.remove(application, user);

		} catch (ServiceException e) {
			// set the application in pending mode
			applicationEventPublisher.publishEvent(new ApplicationFailEvent(application));
		}

		logger.info("Application {} is deleted.", application.getName());

		return ResponseEntity.noContent().build();
	}

	/**
	 * Return detail information about application
	 *
	 * @return
	 * @throws ServiceException
	 */
	@CloudUnitSecurable
	@GetMapping("/{id}")
	public ResponseEntity<?> detail(@PathVariable Integer id) throws ServiceException, CheckException {
		Application application = applicationDAO.findOne(id);
		
        if (application == null) {
            return ResponseEntity.notFound().build();
        }		
		
		ApplicationResource resource = toResource(application);
		return ResponseEntity.ok(resource);
	}

	/**
	 * Return the list of applications for an User
	 */
	@GetMapping
	public ResponseEntity<?> findAllByUser(@RequestParam(defaultValue = "") String name) throws ServiceException {
		User user = this.authentificationUtils.getAuthentificatedUser();
		List<Application> applications = applicationService.findAllByUser(user);

		logger.debug("Number of applications {}", applications.size());
		
		if (!name.equals("")) {
		    applications = applications.stream()
		            .filter(a -> a.getName().equals(name))
		            .collect(Collectors.toList());
		}
		
		Resources<ApplicationResource> resources = new Resources<>(applications.stream()
		        .map(this::toResource)
		        .collect(Collectors.toList()));
		
		resources.add(linkTo(methodOn(ApplicationController.class).findAllByUser(name)).withSelfRel());
		return ResponseEntity.ok(resources);
	}
}
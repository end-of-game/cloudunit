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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.ModuleResource;
import fr.treeptik.cloudunit.dto.PortResource;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Port;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/applications/{id}/modules")
public class ModuleController implements Serializable {
    private static final long serialVersionUID = 1L;

    Locale locale = Locale.ENGLISH;

    private Logger logger = LoggerFactory.getLogger(ModuleController.class);

    @Inject
    private ModuleService moduleService;

    @Inject
    private ApplicationService applicationService;
    
    @Inject
    private ApplicationDAO applicationDAO;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private ApplicationEventPublisher applicationEventPublisher;
    
    private ModuleResource toResource(Module module) {
        ModuleResource resource = new ModuleResource(module);
                
        int id = module.getApplication().getId();
        String moduleName = module.getImage().getName();
        
        try {
            resource.add(linkTo(methodOn(ModuleController.class).getModule(id, moduleName))
                    .withSelfRel());
            resource.add(linkTo(methodOn(ApplicationController.class).detail(id))
                    .withRel("application"));

            resource.add(linkTo(methodOn(ContainerController.class).getContainer(id, module.getContainerID()))
                    .withRel("container"));

            resource.add(linkTo(methodOn(ModuleController.class).getModules(id))
                .withSelfRel());

        } catch (CheckException | ServiceException e) {
            // ignore
        }
        
        return resource;
    }
    
    private PortResource toResource(Module module, Port port) {
        PortResource resource = new PortResource(port);
        
        int id = module.getApplication().getId();
        String moduleName = module.getImage().getName();
        
        resource.add(linkTo(methodOn(ModuleController.class).getModulePort(id, moduleName, port.getContainerValue()))
                .withSelfRel());
        
        resource.add(linkTo(methodOn(ModuleController.class).getModule(id, moduleName))
                .withRel("module"));
        
        return resource;
    }

    /**
     * Add a module to an existing application
     */
    @CloudUnitSecurable
    @PostMapping
    public ResponseEntity<?> addModule(@PathVariable Integer id, @Valid @RequestBody ModuleResource request)
            throws ServiceException, CheckException {
        Application application = applicationDAO.findOne(id);

        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        User user = authentificationUtils.getAuthentificatedUser();
        applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
        
        try {
            Module module = moduleService.create(request.getName(), application, user);
            logger.info("--initModule {} to {} successful--", module.getImage().getName(), application.getName());
            
            ModuleResource resource = toResource(module);
            
            return ResponseEntity
                    .created(URI.create(resource.getId().getHref()))
                    .body(resource); 
        } finally {
            applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
        }
    }
    
    @GetMapping
    @Transactional
    public ResponseEntity<?> getModules(@PathVariable Integer id) {
        Application application = applicationDAO.findOne(id);

        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Module> modules = application.getModules();
        Resources<ModuleResource> resources = new Resources<>(modules.stream()
                .map(this::toResource)
                .collect(Collectors.toList()));
        resources.add(linkTo(methodOn(ModuleController.class).getModules(id))
                .withSelfRel());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{moduleName}")
    public ResponseEntity<?> getModule(@PathVariable Integer id, @PathVariable String moduleName) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasModule(moduleName)) {
            return ResponseEntity.notFound().build();
        }
        
        Module module = application.getModule(moduleName);
        
        return ResponseEntity.ok(toResource(module));
    }
    
    @GetMapping("/{moduleName}/ports")
    public ResponseEntity<?> getModulePorts(@PathVariable Integer id, @PathVariable String moduleName) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasModule(moduleName)) {
            return ResponseEntity.notFound().build();
        }
        
        Module module = application.getModule(moduleName);
        
        List<Port> ports = module.getPorts();
        
        Resources<PortResource> resources = new Resources<>(ports.stream()
                .map(p -> toResource(module, p))
                .collect(Collectors.toList()));
        
        resources.add(linkTo(methodOn(ModuleController.class).getModulePorts(id, moduleName))
                .withSelfRel());
        resources.add(linkTo(methodOn(ModuleController.class).getModule(id, moduleName))
                .withRel("module"));
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{moduleName}/ports/{portNumber}")
    public ResponseEntity<?> getModulePort(
            @PathVariable Integer id,
            @PathVariable String moduleName,
            @PathVariable String portNumber) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasModule(moduleName)) {
            return ResponseEntity.notFound().build();
        }
        
        Module module = application.getModule(moduleName);
        
        if (!module.hasPort(portNumber)) {
            return ResponseEntity.notFound().build();
        }

        Port port = module.getPort(portNumber);
        
        return ResponseEntity.ok(toResource(module, port));
    }
    
    @CloudUnitSecurable
    @PutMapping("/{moduleName}/ports/{portNumber}")
    public ResponseEntity<?> publishPort(
            @PathVariable Integer id,
            @PathVariable String moduleName,
            @PathVariable String portNumber,
            @Valid @RequestBody PortResource request)
            throws ServiceException, CheckException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasModule(moduleName)) {
            return ResponseEntity.notFound().build();
        }
        
        Module module = application.getModule(moduleName);
        
        if (!module.hasPort(portNumber)) {
            return ResponseEntity.notFound().build();
        }
                
        User user = authentificationUtils.getAuthentificatedUser();        
        applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
        Port port = moduleService.publishPort(module, request.getOpen(), portNumber, user);
        applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
        
        return ResponseEntity.ok(toResource(module, port));
    }

    /**
     * Remove a module to an existing application
     */
    @CloudUnitSecurable
    @DeleteMapping("/{moduleName}")
    public ResponseEntity<?> removeModule(@PathVariable Integer id, @PathVariable String moduleName)
            throws ServiceException, CheckException {
        
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasModule(moduleName)) {
            return ResponseEntity.notFound().build();
        }
        
        Module module = application.getModule(moduleName);
        
        // We must be sure there is no running action before starting new one
        User user = authentificationUtils.getAuthentificatedUser();
        authentificationUtils.canStartNewAction(user, application, locale);
        Status previousApplicationStatus = application.getStatus();
        applicationService.setStatus(application, Status.PENDING);
        try {
            moduleService.remove(user, module, true);
            
            logger.info("-- removeModule {} from {} successful-- ",
                    module.getImage().getName(),
                    application.getName());
            
            return ResponseEntity.noContent().build();
        } finally {
            applicationService.setStatus(application, previousApplicationStatus);
        }
    }
    
    @PostMapping(value = "/{moduleName}/run-script", consumes = "multipart/form-data")
    public ResponseEntity<?> runScript(
            @PathVariable Integer id,
            @PathVariable String moduleName,
            @RequestPart("file") MultipartFile file)
            throws ServiceException, CheckException {
        
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasModule(moduleName)) {
            return ResponseEntity.notFound().build();
        }
        
        Module module = application.getModule(moduleName);
        
        String result = moduleService.runScript(module, file);
        
        return ResponseEntity.ok(result);
    }

}

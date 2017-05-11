package fr.treeptik.cloudunit.domain.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.ApplicationState;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.resource.ApplicationResource;
import fr.treeptik.cloudunit.domain.service.ApplicationService;

@Controller
@RequestMapping("/applications")
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ApplicationService applicationService;
    
    public void setApplicationRepository(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    private ApplicationResource toResource(Application application) {
        ApplicationResource resource = new ApplicationResource(application);
        
        String id = application.getId();
        resource.add(linkTo(methodOn(ApplicationController.class).getApplication(id))
                .withSelfRel());
        resource.add(linkTo(methodOn(ServiceController.class).getServices(id))
                .withRel("cu:services"));
        
        if (application.getState() == ApplicationState.STOPPED) {
            resource.add(linkTo(methodOn(ApplicationController.class).start(id))
                    .withRel("cu:start"));
        }
        
        if (application.getState() == ApplicationState.STARTED) {
            resource.add(linkTo(methodOn(ApplicationController.class).stop(id))
                    .withRel("cu:stop"));
        }

        return resource;
    }
    
    @PostMapping
    public ResponseEntity<?> createApplication(@Valid @RequestBody ApplicationResource request) {
        Application application = applicationService.create(request.getName());
        
        ApplicationResource resource = toResource(application);
        return ResponseEntity.created(URI.create(resource.getId().getHref())).body(resource);
    }

    @GetMapping
    public ResponseEntity<?> getApplications() {
        List<Application> applications = applicationRepository.findAll();
        
        Resources<ApplicationResource> resources = new Resources<>(
                applications.stream()
                .map(a -> toResource(a))
                .collect(Collectors.toList()));
        
        resources.add(linkTo(methodOn(ApplicationController.class).getApplications())
                .withSelfRel());
        resources.add(linkTo(methodOn(ApplicationController.class).getApplication(null))
                .withRel("cu:application"));
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplication(@PathVariable String id) {
        return withApplication(id, application ->
            ResponseEntity.ok(toResource(application)));
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<?> start(@PathVariable String id) {
        return withApplication(id, application -> {
            applicationService.startApplication(application);
            
            return ResponseEntity.noContent().build();
        });
    }
    
    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stop(@PathVariable String id) {
        return withApplication(id, application -> {
            applicationService.stopApplication(application);
            
            return ResponseEntity.noContent().build();
        });
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable String id) {
        return withApplication(id, application -> {
            applicationService.delete(application);
            
            return ResponseEntity.noContent().build();            
        });
    }
    
    private ResponseEntity<?> withApplication(String id, Function<Application, ResponseEntity<?>> mapper) {
        return applicationRepository.findOne(id)
                .map(mapper)
                .orElse(ResponseEntity.notFound().build());
    }
}

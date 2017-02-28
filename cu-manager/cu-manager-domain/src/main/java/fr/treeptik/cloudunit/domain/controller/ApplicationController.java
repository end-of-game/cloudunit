package fr.treeptik.cloudunit.domain.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
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
        
        resource.add(linkTo(methodOn(ApplicationController.class).getApplication(application.getId()))
                .withSelfRel());
        resource.add(linkTo(methodOn(ServiceController.class).getServices(application.getId()))
                .withRel("cu:services"));

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
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplication(@PathVariable String id) {
        Optional<Application> application = applicationRepository.findOne(id);
        
        if (!application.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(toResource(application.get()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable String id) {
        Optional<Application> application = applicationRepository.findOne(id);
        
        if (!application.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        applicationService.delete(application.get());
        
        return ResponseEntity.noContent().build();
    }
}

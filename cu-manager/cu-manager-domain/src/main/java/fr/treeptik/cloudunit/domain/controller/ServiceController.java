package fr.treeptik.cloudunit.domain.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.repository.ApplicationRepository;
import fr.treeptik.cloudunit.domain.resource.ServiceResource;
import fr.treeptik.cloudunit.domain.service.ApplicationService;

@Controller
@RequestMapping("/applications/{appId}/services")
public class ServiceController {
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ApplicationService applicationService;
    
    private ServiceResource toResource(Application application, Service service) {
        ServiceResource resource = new ServiceResource(service);
        
        String appId = application.getId();
        String name = service.getName();
        
        resource.add(linkTo(methodOn(ServiceController.class).getService(appId, name))
                .withSelfRel());
        resource.add(linkTo(methodOn(ApplicationController.class).getApplication(appId))
                .withRel("cu:application"));
        resource.add(new Link(service.getContainerUrl(),
                "cu:container"));
        
        return resource;
    }
    
    @PostMapping
    public ResponseEntity<?> addService(@PathVariable String appId, @RequestBody ServiceResource request) {
        Optional<Application> application = applicationRepository.findOne(appId);
        
        if (!application.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Service service = applicationService.addService(application.get(), request.getImageName());
        
        ServiceResource resource = toResource(application.get(), service);
        return ResponseEntity.created(URI.create(resource.getLink(Link.REL_SELF).getHref())).body(resource);
    }

    @GetMapping
    public ResponseEntity<?> getServices(@PathVariable String appId) {
        Optional<Application> application = applicationRepository.findOne(appId);
        
        if (!application.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Collection<Service> services = application.get().getServices();
        
        Resources<ServiceResource> resources = new Resources<>(services.stream()
                .map(s -> toResource(application.get(), s))
                .collect(Collectors.toList()));
        
        resources.add(linkTo(methodOn(ServiceController.class).getServices(appId))
                .withSelfRel());
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<?> getService(@PathVariable String appId, @PathVariable String name) {
        Optional<Application> application = applicationRepository.findOne(appId);
        
        if (!application.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Service> service = application.get().getService(name);
        
        if (!service.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(toResource(application.get(), service.get()));
    }
}

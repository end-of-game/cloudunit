package fr.treeptik.cloudunit.domain.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
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
        if(service.getContainerUrl() != null) {
        	resource.add(new Link(service.getContainerUrl(), "cu:container"));
        }
        return resource;
    }
    
    @PostMapping
    public ResponseEntity<?> addService(@PathVariable String appId, @RequestBody ServiceResource request) {
        return withApplication(appId, application -> {
            Service service = applicationService.addService(application, request.getImageName());
            
            ServiceResource resource = toResource(application, service);
            return ResponseEntity.created(URI.create(resource.getLink(Link.REL_SELF).getHref())).body(resource);            
        });
    }

    @GetMapping
    public ResponseEntity<?> getServices(@PathVariable String appId) {
        return withApplication(appId, application -> {
            Resources<ServiceResource> resources = new Resources<>(application.getServices().stream()
                    .map(s -> toResource(application, s))
                    .collect(Collectors.toList()));
            
            resources.add(linkTo(methodOn(ServiceController.class).getServices(appId))
                    .withSelfRel());
            
            return ResponseEntity.ok(resources);            
        });
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<?> getService(@PathVariable String appId, @PathVariable String name) {
        return withService(appId, name, (application, service) ->
            ResponseEntity.ok(toResource(application, service)));
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<?> removeService(@PathVariable String appId, @PathVariable String name) {
        return withService(appId, name, (application, service) -> {
            applicationService.removeService(application, service);
            
            return ResponseEntity.noContent().build();
        });
    }
    
    private ResponseEntity<?> withApplication(String appId, Function<Application, ResponseEntity<?>> mapper) {
        return applicationRepository.findOne(appId)
                .map(application -> mapper.apply(application))
                .orElse(ResponseEntity.notFound().build());
    }
    
    private ResponseEntity<?> withService(String appId, String name,
            BiFunction<Application, Service, ResponseEntity<?>> mapper) {
        return applicationRepository.findOne(appId)
                .flatMap(application -> application.getService(name)
                        .map(service -> mapper.apply(application, service)))
                .orElse(ResponseEntity.notFound().build());
    }
}

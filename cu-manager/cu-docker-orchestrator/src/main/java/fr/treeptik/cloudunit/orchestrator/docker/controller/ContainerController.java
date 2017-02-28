package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

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

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;

@Controller
@RequestMapping("/containers")
public class ContainerController {
    @Autowired
    private DockerService dockerService;
    
    @Autowired
    private ContainerRepository containerRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    private ContainerResource toResource(Container container) {
        ContainerResource resource = new ContainerResource(container);
        
        resource.add(linkTo(methodOn(ContainerController.class).getContainer(container.getName()))
                .withSelfRel());
        
        return resource;
    }
    
    @PostMapping
    public ResponseEntity<?> createContainer(@Valid @RequestBody ContainerResource request) {
        Image image = imageRepository.findByName(request.getImageName())
                .orElseThrow(() -> new IllegalArgumentException());
        
        Container container = dockerService.createContainer(request.getName(), image);
        
        ContainerResource resource = toResource(container);
        
        return ResponseEntity.created(URI.create(resource.getLink(Link.REL_SELF).getHref()))
                .body(resource);
    }
    
    @GetMapping
    public ResponseEntity<?> getContainers() {
        List<Container> containers = containerRepository.findAll();
        
        Resources<ContainerResource> resources = new Resources<>(
                containers.stream()
                .map(c -> toResource(c))
                .collect(Collectors.toList()));
        
        resources.add(linkTo(methodOn(ContainerController.class).getContainers())
                .withSelfRel());
        resources.add(linkTo(methodOn(ContainerController.class).getContainer(null))
                .withRel("cu:container"));
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<?> getContainer(@PathVariable String name) {
        return withContainer(name, container -> {
            return ResponseEntity.ok(toResource(container));
        });
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteContainer(@PathVariable String name) {
        return withContainer(name, container -> {
            dockerService.deleteContainer(container);
            return ResponseEntity.noContent().build();
        });
    }
    
    private ResponseEntity<?> withContainer(String name, Function<Container, ResponseEntity<?>> mapper) {
        return containerRepository.findByName(name)
                .map(mapper)
                .orElse(ResponseEntity.notFound().build());
    }
}

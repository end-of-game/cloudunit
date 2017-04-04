package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerDependencyResource;

@Controller
@RequestMapping("/containers/{name}/dependencies")
public class ContainerDependencyController {
    @Autowired
    private ContainerRepository containerRepository;
    
    @Autowired
    private DockerService dockerService;
    
    private ContainerDependencyResource toResource(Container container, Container dependency) {
        String depName = dependency.getName();
        
        return toResource(container, depName);
    }

    private ContainerDependencyResource toResource(Container container, String depName) {
        ContainerDependencyResource resource = new ContainerDependencyResource();
        
        resource.add(linkTo(methodOn(ContainerDependencyController.class).getDependency(container.getName(), depName))
                .withSelfRel());
        resource.add(linkTo(methodOn(ContainerController.class).getContainer(container.getName()))
                .withRel("cu:dependency"));
        
        return resource;
    }
    
    @PostMapping
    public ResponseEntity<?> addDependency(@PathVariable String name, @Valid @RequestBody ContainerDependencyResource request) {
        return withContainer(name, container -> {
            Container dependency = containerRepository.findByName(getNameFromUri(request))
                    .orElseThrow(() -> new IllegalArgumentException("No such container"));
            
            dockerService.addDependency(container, dependency);
            
            ContainerDependencyResource resource = toResource(container, dependency);
            return ResponseEntity.created(URI.create(resource.getLink(Link.REL_SELF).getHref()))
                    .body(resource);
        });
    }
    
    @GetMapping
    public ResponseEntity<?> getDependencies(@PathVariable String name) {
        return withContainer(name, container -> {
            Resources<ContainerDependencyResource> resources = new Resources<>(container.getDependencies().stream()
                    .map(d -> toResource(container, d))
                    .collect(Collectors.toList()));
            
            resources.add(linkTo(methodOn(ContainerDependencyController.class).getDependencies(name))
                    .withSelfRel());
            resources.add(linkTo(methodOn(ContainerController.class).getContainer(name))
                    .withRel("cu:container"));
            
            return ResponseEntity.ok(resources);
        });
    }
    
    @GetMapping("/{depName}")
    public ResponseEntity<?> getDependency(@PathVariable String name, @PathVariable String depName) {
        return withDependency(name, depName, (container, dependency) -> ResponseEntity.ok(toResource(container, dependency)));
    }
    
    @DeleteMapping("/{depName}")
    public ResponseEntity<?> removeDependency(@PathVariable String name, @PathVariable String depName) {
        return withDependency(name, depName, (container, dependency) -> {
            dockerService.removeDependency(container, dependency);
            
            return ResponseEntity.noContent().build();
        });
    }
    
    
    private ResponseEntity<?> withDependency(String name, String depName, BiFunction<Container, String, ResponseEntity<?>> mapper) {
        return containerRepository.findByName(name)
                .filter(container -> container.hasDependency(depName))
                .map(container -> mapper.apply(container, depName))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> withContainer(String name, Function<Container, ResponseEntity<?>> mapper) {
        return containerRepository.findByName(name)
                .map(mapper)
                .orElse(ResponseEntity.notFound().build());
    }
    
    private String getNameFromUri(ContainerDependencyResource resource) {
        String uriTemplate = linkTo(methodOn(ContainerController.class).getContainer(null)).withSelfRel().getHref();
        String uri = resource.getLink("cu:dependency").getHref();
        
        String name = new AntPathMatcher().extractUriTemplateVariables(uriTemplate, uri).get("name");
        return name;
    }
}

package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
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
import fr.treeptik.cloudunit.orchestrator.core.Mount;
import fr.treeptik.cloudunit.orchestrator.core.Volume;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.repository.VolumeRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.resource.MountResource;

@Controller
@RequestMapping("/containers/{name}/mounts")
public class MountController {
    @Autowired
    private ContainerRepository containerRepository;
    
    @Autowired
    private VolumeRepository volumeRepository;
    
    @Autowired
    private DockerService dockerService;
    
    private MountResource toResource(Container container, Mount mount) {
        MountResource resource = new MountResource(mount);
        
        resource.add(linkTo(methodOn(MountController.class).getMount(container.getName(), mount.getVolumeName()))
                .withSelfRel());
        resource.add(linkTo(methodOn(VolumeController.class).getVolume(mount.getVolumeName()))
                .withRel("cu:volume"));
        
        return resource;
    }

    @PostMapping
    public ResponseEntity<?> addMount(@PathVariable String name, @Valid @RequestBody MountResource request) {
        return withContainer(name, container -> {
            String volumeUriTemplate = linkTo(methodOn(VolumeController.class).getVolume(null)).withSelfRel().getHref();
            String volumeUri = request.getVolume().getLink(Link.REL_SELF).getHref();
            
            String volumeName = new AntPathMatcher().extractUriTemplateVariables(volumeUriTemplate, volumeUri).get("name");
            
            Optional<Volume> volume = volumeRepository.findByName(volumeName);
            
            if (!volume.isPresent()) {
                throw new IllegalArgumentException("Unknown volume");
            }
            
            Mount mount = dockerService.mountVolume(container, volume.get(), request.getMountPoint());
            MountResource resource = toResource(container, mount);
            return ResponseEntity.created(URI.create(resource.getLink(Link.REL_SELF).getHref()))
                    .body(resource);
        });
    }
    
    @GetMapping
    public ResponseEntity<?> getMounts(@PathVariable String name) {
        return withContainer(name, container -> {
            Collection<Mount> mounts = container.getMounts();
            Resources<MountResource> resources = new Resources<>(mounts.stream()
                    .map(m -> toResource(container, m))
                    .collect(Collectors.toList()));
            
            resources.add(linkTo(methodOn(MountController.class).getMounts(name))
                    .withSelfRel());
            resources.add(linkTo(methodOn(ContainerController.class).getContainer(name))
                    .withRel("cu:container"));
            
            return ResponseEntity.ok(resources);
        });
    }
    
    @GetMapping("/{volumeName}")
    public ResponseEntity<?> getMount(@PathVariable String name, @PathVariable String volumeName) {
        return withMount(name, volumeName, (container, mount) -> {
            return ResponseEntity.ok(toResource(container, mount));
        });
    }
    
    @DeleteMapping("/{volumeName}")
    public ResponseEntity<?> removeMount(@PathVariable String name, @PathVariable String volumeName) {
        return withMount(name, volumeName, (container, mount) -> {
            dockerService.unmountVolume(container, mount);
            return ResponseEntity.noContent().build();
        });
    }
    
    private ResponseEntity<?> withContainer(String name, Function<Container, ResponseEntity<?>> mapper) {
        return containerRepository.findByName(name)
                .map(mapper)
                .orElse(ResponseEntity.notFound().build());
    }    

    private ResponseEntity<?> withMount(String name, String volumeName, BiFunction<Container, Mount, ResponseEntity<?>> mapper) {
        return containerRepository.findByName(name)
                .flatMap(container -> container.getMount(volumeName).map(volume -> mapper.apply(container, volume)))
                .orElse(ResponseEntity.notFound().build());
    }
}

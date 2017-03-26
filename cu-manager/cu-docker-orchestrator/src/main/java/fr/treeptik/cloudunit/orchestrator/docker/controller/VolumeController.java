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

import fr.treeptik.cloudunit.orchestrator.core.Volume;
import fr.treeptik.cloudunit.orchestrator.docker.repository.VolumeRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.resource.VolumeResource;

@Controller
@RequestMapping("/volumes")
public class VolumeController {
    @Autowired
    private VolumeRepository volumeRepository;
    
    @Autowired
    private DockerService dockerService;
    
    private VolumeResource toResource(Volume volume) {
        VolumeResource resource = new VolumeResource(volume);
        
        resource.add(linkTo(methodOn(VolumeController.class).getVolume(volume.getName()))
                .withSelfRel());
        
        return resource;
    }
    
    @PostMapping
    public ResponseEntity<?> createVolume(@Valid @RequestBody VolumeResource request) {
        String name = request.getName();
        
        if (volumeRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Volume name already in use");
        }
        
        Volume volume = dockerService.createVolume(name);
        
        VolumeResource resource = toResource(volume);
        
        return ResponseEntity.created(URI.create(resource.getLink(Link.REL_SELF).getHref()))
                .body(resource);
    }
    
    @GetMapping
    public ResponseEntity<?> getVolumes() {
        List<Volume> volumes = volumeRepository.findAll();
        
        Resources<VolumeResource> resources = new Resources<VolumeResource>(volumes.stream()
                .map(v -> toResource(v))
                .collect(Collectors.toList()));
        
        resources.add(linkTo(methodOn(VolumeController.class).getVolumes())
                .withSelfRel());
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<?> getVolume(@PathVariable String name) {
        return withVolume(name, volume -> {
            return ResponseEntity.ok(toResource(volume));
        });
    }
    
    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteVolume(@PathVariable String name) {
        return withVolume(name, volume -> {
            dockerService.deleteVolume(volume);
            return ResponseEntity.noContent().build();
        });
    }
    
    private ResponseEntity<?> withVolume(String name, Function<Volume, ResponseEntity<?>> mapper) {
        return volumeRepository.findByName(name)
                .map(mapper)
                .orElse(ResponseEntity.notFound().build());
    }
}

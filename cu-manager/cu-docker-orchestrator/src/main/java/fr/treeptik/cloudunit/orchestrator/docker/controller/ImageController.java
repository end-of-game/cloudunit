package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.resource.ImageResource;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;

@Controller
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageRepository imageRepository;
    
    private ImageResource toResource(Image image) {
        String name = image.getName();
        
        ImageResource resource = new ImageResource(image);
        
        resource.add(linkTo(methodOn(ImageController.class).getImage(name))
                .withSelfRel());
        resource.add(linkTo(methodOn(ImageController.class).getVariables(name))
                .withRel("cu:variables"));
        
        return resource;
    }
    
    private VariableResource toResource(Image image, Variable variable) {
        String name = image.getName();
        String key = variable.getKey();
        
        VariableResource resource = new VariableResource(variable);
        
        resource.add(linkTo(methodOn(ImageController.class).getVariable(name, key))
                .withSelfRel());
        resource.add(linkTo(methodOn(ImageController.class).getImage(name))
                .withRel("cu:image"));
        
        return resource;
    }
    
    @GetMapping
    public ResponseEntity<?> getImages() {
        List<Image> images = imageRepository.findAll();
        
        Resources<ImageResource> resources = new Resources<>(
                images.stream()
                .map(i -> toResource(i))
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{name}")
    public ResponseEntity<?> getImage(@PathVariable String name) {
        return imageRepository.findByName(name)
                .map(image -> ResponseEntity.ok(toResource(image)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{name}/variables")
    public ResponseEntity<?> getVariables(@PathVariable String name) {
        return imageRepository.findByName(name)
                .map(image -> {
                    Resources<VariableResource> resources = new Resources<>(image.getVariables().stream()
                    .map(v -> toResource(image, v))
                    .collect(Collectors.toList()));
                    
                    // TODO add links
                    
                    return ResponseEntity.ok(resources);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{name}/variables/{key}")
    public ResponseEntity<?> getVariable(@PathVariable String name, @PathVariable String key) {
        return imageRepository.findByName(name)
                .flatMap(image -> {
                    return image.getVariable(key).map(v -> ResponseEntity.ok(toResource(image, v)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

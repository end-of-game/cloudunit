package fr.treeptik.cloudunit.orchestrator.docker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.resource.ImageResource;

@Controller
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageRepository imageRepository;
    
    private ImageResource toResource(Image image) {
        ImageResource resource = new ImageResource(image);
        
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
}

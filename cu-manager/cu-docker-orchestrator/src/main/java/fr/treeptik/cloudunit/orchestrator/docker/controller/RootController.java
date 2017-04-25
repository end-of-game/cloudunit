package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {
    private static final String DUMMY = "";
    
    @GetMapping
    public ResponseEntity<?> root() {
        return ResponseEntity.ok(new Resource<>(DUMMY,
                linkTo(methodOn(RootController.class).root())
                    .withSelfRel(),
                linkTo(methodOn(ContainerController.class).getContainers())
                    .withRel("cu:containers"),
                linkTo(methodOn(ImageController.class).getImages())
                    .withRel("cu:images")));
    }
}

package fr.treeptik.cloudunit.domain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.domain.resource.ImageEvent;

@Controller
@RequestMapping("/image-events")
public class ImageEventController {
    @PostMapping
    public ResponseEntity<?> handle(ImageEvent event) {
        // TODO
        return null;
    }
}

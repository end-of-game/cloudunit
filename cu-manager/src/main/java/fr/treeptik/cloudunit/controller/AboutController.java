package fr.treeptik.cloudunit.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.dto.AboutResource;

@Controller
@RequestMapping("/about")
public class AboutController {
    @Value("${api.version}")
    private String version;
    @Value("${api.timestamp}")
    private String timestamp;
    
    @GetMapping
    public ResponseEntity<?> about() {
        AboutResource resource = new AboutResource(version, timestamp);
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping("/version")
    public ResponseEntity<?> version() {
        return ResponseEntity.ok(version);
    }
    
    @GetMapping("/build-timestamp")
    public ResponseEntity<?> timestamp() {
        return ResponseEntity.ok(timestamp);
    }
}

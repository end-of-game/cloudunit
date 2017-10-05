package fr.treeptik.cloudunit.controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import fr.treeptik.cloudunit.model.Statistique;
import fr.treeptik.cloudunit.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.treeptik.cloudunit.dto.AboutResource;

@Controller
@RequestMapping("/about")
public class AboutController {

    @Value("${api.version}")
    private String version;

    @Value("${api.timestamp}")
    private String timestamp;

    @Inject
    private StatistiqueService statistiqueService;

    @PostConstruct
    public void init() {
        statistiqueService.boot();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> about() {
        Statistique statistique = statistiqueService.last();
        AboutResource resource = new AboutResource(
                version,
                timestamp,
                statistique.getStartTimeAsString());
        return ResponseEntity.ok(resource);
    }
}

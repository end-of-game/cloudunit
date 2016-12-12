package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.dto.AboutResource;
import fr.treeptik.cloudunit.dto.HomepageResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by nicolas on 12/12/2016.
 */
@Controller
@RequestMapping("/homepage")
public class HomepageController {

    @Value("#{environment.CU_DOMAIN_JENKINS}")
    private String jenkins;

    @Value("#{environment.CU_DOMAIN_GITLAB}")
    private String gitlab;

    @Value("#{environment.CU_DOMAIN_NEXUS}")
    private String nexus;

    @Value("#{environment.CU_DOMAIN_KIBANA}")
    private String kibana;

    @Value("#{environment.CU_DOMAIN_SONAR}")
    private String sonar;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> about() {
        HomepageResource resource = new HomepageResource(jenkins, gitlab, kibana, nexus, sonar);
        return ResponseEntity.ok(resource);
    }

}

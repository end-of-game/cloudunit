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

    @Value("#{environment.CU_JENKINS_DOMAIN}")
    private String jenkins;

    @Value("#{environment.CU_GITLAB_DOMAIN}")
    private String gitlab;

    @Value("#{environment.CU_NEXUS_DOMAIN}")
    private String nexus;

    @Value("#{environment.CU_KIBANA_DOMAIN}")
    private String kibana;

    @Value("#{environment.CU_SONAR_DOMAIN}")
    private String sonar;

    @Value("#{environment.CU_LETSCHAT_DOMAIN}")
    private String letschat;

    @RequestMapping(value = "/friends", method = RequestMethod.GET)
    public ResponseEntity<?> listFriends() {
        HomepageResource resource = new HomepageResource(jenkins, gitlab, kibana, nexus, sonar, letschat);
        return ResponseEntity.ok(resource);
    }

}

package fr.treeptik.cloudunit.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.treeptik.cloudunit.dto.HomepageResource;

/**
 * Created by nicolas on 12/12/2016.
 */
@Controller
@RequestMapping("/homepage")
public class HomepageController {

    @Value("#{environment.CU_JENKINS_DOMAIN ?: 'jenkins.cloudunit.dev'}")
    private String jenkins;

    @Value("#{environment.CU_GITLAB_DOMAIN ?: 'gitlab.cloudunit.dev'}")
    private String gitlab;

    @Value("#{environment.CU_NEXUS_DOMAIN ?: 'nexus.cloudunit.dev'}")
    private String nexus;

    @Value("#{environment.CU_KIBANA_DOMAIN ?: 'kibana.cloudunit.dev'}")
    private String kibana;

    @Value("#{environment.CU_SONAR_DOMAIN ?: 'sonar.cloudunit.dev'}")
    private String sonar;

    @Value("#{environment.CU_LETSCHAT_DOMAIN ?: 'letschat.cloudunit.dev'}")
    private String letschat;

    @RequestMapping(value = "/friends", method = RequestMethod.GET)
    public ResponseEntity<?> listFriends() {
        HomepageResource resource = new HomepageResource(jenkins, gitlab, kibana, nexus, sonar, letschat);
        return ResponseEntity.ok(resource);
    }

}

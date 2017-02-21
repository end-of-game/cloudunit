package fr.treeptik.cloudunit.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.DeploymentResource;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Deployment;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/applications/{id}/deployments")
public class DeploymentController {
    @Inject
    private ApplicationDAO applicationDAO;
    
    @Inject
    private ApplicationService applicationService;
    
    @Inject
    private AuthentificationUtils authentificationUtils;
    
    private DeploymentResource toResource(Deployment deployment) {
        DeploymentResource resource = new DeploymentResource(deployment);
        
        Integer appId = deployment.getApplication().getId();
        try {
            resource.add(linkTo(methodOn(DeploymentController.class).findOne(appId, deployment.getId()))
                    .withSelfRel());
            resource.add(linkTo(methodOn(ApplicationController.class).detail(appId))
                    .withRel("application"));
            resource.add(new Link(deployment.getLocation(), "open"));
        } catch (CheckException | ServiceException e) {
            // shouldn't happen
        }
        
        return resource;
    }
    
    /**
     * Deploy a web application
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> deploy(
            @PathVariable Integer id,
            @RequestPart("file") MultipartFile fileUpload)
            throws IOException, ServiceException, CheckException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }

        // We must be sure there is no running action before starting new one
        User user = authentificationUtils.getAuthentificatedUser();
        authentificationUtils.canStartNewAction(user, application, Locale.ENGLISH);

        Deployment deployment = applicationService.deploy(fileUpload, application);

        return ResponseEntity.ok(toResource(deployment));
    }

    @GetMapping
    public ResponseEntity<?> findAllByApplication(@PathVariable Integer id) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Deployment> deployments = application.getDeployments();
        Resources<DeploymentResource> resources = new Resources<>(deployments.stream()
                .map(this::toResource)
                .collect(Collectors.toList()));
        
        resources.add(linkTo(methodOn(DeploymentController.class).findAllByApplication(id))
                .withSelfRel());
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{deploymentId}")
    public ResponseEntity<?> findOne(@PathVariable Integer id, @PathVariable Integer deploymentId) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        return application.getDeployments().stream()
                .filter(d -> d.getId() == deploymentId)
                .findAny()
                .map(this::toResource)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());        
    }

}

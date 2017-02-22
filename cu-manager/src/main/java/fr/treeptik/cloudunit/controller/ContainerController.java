package fr.treeptik.cloudunit.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import fr.treeptik.cloudunit.model.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.ContainerResource;
import fr.treeptik.cloudunit.dto.EnvironmentVariableResource;
import fr.treeptik.cloudunit.dto.VolumeResource;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.service.VolumeService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/applications/{id}/containers")
public class ContainerController {
    @Inject
    private AuthentificationUtils authentificationUtils;
    
    @Inject
    private ApplicationDAO applicationDAO;
    
    @Inject
    private EnvironmentService environmentService;
    
    @Inject
    private VolumeService volumeService;
    
    @Inject
    private DockerService dockerService;
    
    @Inject
    private ApplicationEventPublisher applicationEventPublisher;
    
    private ContainerResource toResource(Application application, Container container) {
        ContainerResource resource = new ContainerResource(container);

        try {
            Integer applicationId = application.getId();
            String containerId = container.getContainerID();
            resource.add(linkTo(methodOn(ContainerController.class).getContainer(applicationId, containerId))
                    .withSelfRel());

            resource.add(linkTo(methodOn(ContainerController.class).displayEnv(applicationId, containerId))
                    .withRel("env"));

        } catch (CheckException | ServiceException e) {
            // ignore
        }

        return resource;
    }
    
    private EnvironmentVariableResource toResource(
            Application application, Container container,
            EnvironmentVariable variable) {
        EnvironmentVariableResource resource = new EnvironmentVariableResource(variable);
        
        return resource;
    }
    
    @GetMapping
    @Transactional
    public ResponseEntity<?> getContainers(@PathVariable Integer id) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Container> containers = application.getContainers();
        
        Resources<ContainerResource> resources = new Resources<>(containers.stream()
                .map(c -> toResource(application, c))
                .collect(Collectors.toList()));

        resources.add(linkTo(methodOn(ContainerController.class).getContainers(id))
                .withSelfRel());

        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{containerId}")
    @Transactional
    public ResponseEntity<?> getContainer(@PathVariable Integer id, @PathVariable String containerId) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Container container = application.getContainer(containerId);
        
        return ResponseEntity.ok(toResource(application, container));
    }
    
    /**
     * Display env variables for a container
     */
    @CloudUnitSecurable
    @GetMapping("/{containerId}/env")
    @Transactional
    public ResponseEntity<?> displayEnv(
            @PathVariable Integer id,
            @PathVariable String containerId)
            throws ServiceException, CheckException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }        
        
        try {
            User user = authentificationUtils.getAuthentificatedUser();
            String env = dockerService.execCommand(containerId, RemoteExecAction.GATHER_CU_ENV.getCommand() + " " + user.getLogin());

            return ResponseEntity.ok(EnvironmentVariableResource.fromEnv(env));
        } catch (FatalDockerJSONException e) {
            throw new ServiceException(application.getName() + ", " + containerId, e);
        }
    }
    
    @GetMapping("/{containerId}/env-vars/")
    @Transactional
    public ResponseEntity<?> getEnvironmentVariables(@PathVariable Integer id, @PathVariable String containerId)
            throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Container container = application.getContainer(containerId);
        
        List<EnvironmentVariable> variables =
                environmentService.loadEnvironnmentsByContainer(container.getName());
        
        Resources<EnvironmentVariableResource> resources = new Resources<>(variables.stream()
                .map(v -> toResource(application, container, v))
                .collect(Collectors.toList()));
        
        resources.add(linkTo(methodOn(ContainerController.class).getEnvironmentVariables(id, containerId))
                .withSelfRel());
        resources.add(linkTo(methodOn(ApplicationController.class).detail(id))
                .withRel("application"));
        
        return ResponseEntity.ok(resources);
    }
    
    // FIXME this method should probably be combined with the update method
    @PostMapping("/{containerId}/env-vars")
    @Transactional
    public ResponseEntity<?> addEnvironmentVariable(
            @PathVariable Integer id,
            @PathVariable String containerId,
            @Valid @RequestBody EnvironmentVariableResource request) throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Container container = application.getContainer(containerId);
        
        User user = authentificationUtils.getAuthentificatedUser();

        EnvironmentVariable variable = new EnvironmentVariable(request.getKey(), request.getValue());
        
        variable = environmentService.save(user, variable, application.getName(), container.getName());
        // FIXME The next two instructions shouldn't be here. They might not even be necessary. X-P
        applicationEventPublisher.publishEvent(new ServerStartEvent(application.getServer()));
        applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));

        EnvironmentVariableResource resource = toResource(application, container, variable);
        return ResponseEntity.created(URI.create(resource.getId().getHref())).body(resource);
    }
    
    @GetMapping("/{containerId}/env-vars/{key}")
    @Transactional
    public ResponseEntity<?> getEnvironmentVariable(
            @PathVariable Integer id,
            @PathVariable String containerId,
            @PathVariable String key) throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Container container = application.getContainer(containerId);

        Optional<EnvironmentVariable> variable = environmentService.loadEnvironnmentsByContainer(container.getName()).stream()
                .filter(v -> v.getKey().equals(key))
                .findAny();
        
        if (!variable.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(toResource(application, container, variable.get()));
    }
    
    @PutMapping("/{containerId}/env-vars/{key}")
    @Transactional
    public ResponseEntity<?> updateEnvironmentVariable(
            @PathVariable Integer id,
            @PathVariable String containerId,
            @PathVariable String key,
            @Valid @RequestBody EnvironmentVariableResource request) throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Container container = application.getContainer(containerId);

        Optional<EnvironmentVariable> variableO = environmentService.loadEnvironnmentsByContainer(container.getName()).stream()
                .filter(v -> v.getKey().equals(key))
                .findAny();
        
        if (!variableO.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        EnvironmentVariable variable = variableO.get();
        variable.setValue(request.getValue());
        
        User user = authentificationUtils.getAuthentificatedUser();
        variable = environmentService.update(user, variable, application.getName(), container.getName());

        return ResponseEntity.ok(toResource(application, container, variable));
    }
    
    @DeleteMapping("/{containerId}/env-vars/{key}")
    @Transactional
    public ResponseEntity<?> deleteEnvironmentVariable(
            @PathVariable Integer id,
            @PathVariable String containerId,
            @PathVariable String key) throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Container container = application.getContainer(containerId);

        Optional<EnvironmentVariable> variableO = environmentService.loadEnvironnmentsByContainer(container.getName()).stream()
                .filter(v -> v.getKey().equals(key))
                .findAny();
        
        if (!variableO.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        EnvironmentVariable variable = variableO.get();
        User user = authentificationUtils.getAuthentificatedUser();
        environmentService.delete(user, variable.getId(), application.getName(), container.getName());
        
        return ResponseEntity.noContent().build();
    }
    
    @RequestMapping(value = "/{containerId}/volumes", method = RequestMethod.GET)
    public ResponseEntity<?> getVolumes(
            @PathVariable Integer id,            
            @PathVariable String containerId)
            throws ServiceException, CheckException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null || !application.hasContainer(containerId)) {
            return ResponseEntity.notFound().build();
        }
        
        Container container = application.getContainer(containerId);

        List<VolumeResource> resource = volumeService.loadAllByContainerName(container.getName()).stream()
                        .map(VolumeResource::new)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(resource);
    }
}

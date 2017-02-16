package fr.treeptik.cloudunit.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.ContainerResource;
import fr.treeptik.cloudunit.dto.EnvironmentVariableResource;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Container;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/applications/{id}/containers/{containerId}")
public class ContainerController {
    @Inject
    private AuthentificationUtils authentificationUtils;
    
    @Inject
    private ApplicationDAO applicationDAO;
    
    @Inject
    private DockerService dockerService;
    
    private ContainerResource toResource(Application application, Container container) {
        ContainerResource resource = new ContainerResource(container);
        
        return resource;
    }
    
    @GetMapping
    public ResponseEntity<?> getContainers(@PathVariable Integer id) {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Container> containers = application.getContainers();
        
        Resources<ContainerResource> resources = new Resources<>(containers.stream()
                .map(c -> toResource(application, c))
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{containerId}")
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
    public ResponseEntity<?> getEnvironmentVariables(@PathVariable Integer id, @PathVariable String containerId) {
        // TODO
        return null;
    }
    
}

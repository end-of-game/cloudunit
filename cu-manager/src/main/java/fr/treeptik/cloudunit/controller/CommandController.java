package fr.treeptik.cloudunit.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.dao.ApplicationDAO;
import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.CommandResource;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Container;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.CommandService;

@Controller
@RequestMapping("/applications/{id}/containers/{containerId}/commands")
public class CommandController {
    private final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Inject
    private CommandService commandService;

    @Inject
    private ApplicationService applicationService;
    
    @Inject
    private ApplicationDAO applicationDAO;

    private CommandResource toResource(Application application, Container container, Command command) {
        CommandResource resource = new CommandResource(command);
        
        try {
            resource.add(linkTo(methodOn(CommandController.class).findOne(application.getId(), container.getContainerID(), command.getName()))
                    .withSelfRel());
        } catch (ServiceException e) {
            // ignore
        }
        
        return resource;
    }

    @GetMapping
    public ResponseEntity<?> listCommandByImage(
            @PathVariable Integer id,
            @PathVariable String containerId) throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Container> container = application.getContainers().stream()
                .filter(c -> c.getContainerID().equals(containerId))
                .findAny();
        
        if (!container.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Command> commands = commandService.listCommandByContainer(containerId);
        
        Resources<CommandResource> resources = new Resources<>(commands.stream()
                .map(c -> toResource(application, container.get(), c))
                .collect(Collectors.toList()));
        
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{commandName}")
    public ResponseEntity<?> findOne(
            @PathVariable Integer id,
            @PathVariable String containerId,
            @PathVariable String commandName) throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }

        Optional<Container> container = application.getContainers().stream()
                .filter(c -> c.getContainerID().equals(containerId))
                .findAny();
        
        if (!container.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return commandService.listCommandByContainer(containerId).stream()
            .filter(c -> c.getName().equals(commandName))
            .findAny()
            .map(c -> toResource(application, container.get(), c))
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/{commandName}/run", consumes = "application/json")
    public ResponseEntity<?> execCommand(
            @PathVariable Integer id,
            @PathVariable String containerId,
            @PathVariable String commandName,
            @RequestBody List<String> arguments) throws ServiceException {
        Application application = applicationDAO.findOne(id);
        
        if (application == null) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Command> command = commandService.listCommandByContainer(containerId).stream()
            .filter(c -> c.getName().equals(commandName))
            .findAny();
        
        if (!command.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            applicationService.setStatus(application, Status.PENDING);
            String output = commandService.execCommand(containerId, command.get(), arguments);
            logger.debug(output);
        } finally {
            applicationService.setStatus(application, Status.START);
        }
        return ResponseEntity.noContent().build();
    }
}

package fr.treeptik.cloudunit.orchestrator.docker.controller;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

@Controller
@RequestMapping("/containers/{name}/variables")
public class VariableController {
    @Autowired
    private DockerService dockerService;

    @Autowired
    private ContainerRepository containerRepository;

    private VariableResource toResource(String containerName, Variable variable) {
        VariableResource resource = new VariableResource(variable);
        resource.add(linkTo(methodOn(VariableController.class).getVariable(containerName, variable.getKey()))
                .withSelfRel());
        return resource;
    }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable String name, @Valid @RequestBody VariableResource request) {
        return withContainer(name, container -> {
            Variable variable = dockerService.addVariable(container, request.getKey(), request.getValue());
            VariableResource resource = toResource(container.getName(), variable);
            return ResponseEntity.created(URI.create(resource.getLink(Link.REL_SELF).getHref()))
                    .body(resource);
        });
    }

    @GetMapping
    public ResponseEntity<?> getVariables(@PathVariable String name) {
        return withContainer(name, container -> {
            Resources<VariableResource> resources = new Resources<>(
                    container.getVariables().stream()
                            .map(c -> toResource(name, c))
                            .collect(Collectors.toList()));
            resources.add(linkTo(methodOn(VariableController.class).getVariables(name))
                    .withSelfRel());
            resources.add(linkTo(methodOn(ContainerController.class).getContainer(name))
                    .withRel("cu:container"));
            return ResponseEntity.ok(resources);
        });
    }

    @GetMapping("/{variableKey}")
    public ResponseEntity<?> getVariable(@PathVariable String name, @PathVariable String variableKey) {
        return withVariable(name, variableKey, (container, variable) -> {
           return ResponseEntity.ok(toResource(container.getName(), variable));
        });
    }

    private ResponseEntity<?> withContainer(String name, Function<Container, ResponseEntity<?>> mapper) {
        return containerRepository.findByName(name)
                .map(mapper)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<?> withVariable(String name, String variableKey, BiFunction<Container, Variable, ResponseEntity<?>> mapper) {
        /* Equivalent code without flatMap
        Optional<Container> container = containerRepository.findByName(name);
        if (!container.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Variable> variable = container.get().getVariable(variableKey);
        if (!variable.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return mapper.apply(container.get(), variable.get());
        */

        return containerRepository.findByName(name)
            .flatMap(container -> {
                return container.getVariable(variableKey)
                        .map(variable -> mapper.apply(container, variable));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}

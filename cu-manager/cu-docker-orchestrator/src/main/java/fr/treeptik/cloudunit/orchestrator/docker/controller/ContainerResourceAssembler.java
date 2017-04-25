package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;

@Component
public class ContainerResourceAssembler extends ResourceAssemblerSupport<Container, ContainerResource> {
    public ContainerResourceAssembler() {
        super(ContainerController.class, ContainerResource.class);
    }

    @Override
    public ContainerResource toResource(Container container) {
        ContainerResource resource = new ContainerResource(container);
        
        String name = container.getName();
        
        resource.add(linkTo(methodOn(ContainerController.class).getContainer(name))
                .withSelfRel());

        resource.add(linkTo(methodOn(VariableController.class).getVariables(name))
                .withRel("cu:variables"));
        resource.add(linkTo(methodOn(MountController.class).getMounts(name))
                .withRel("cu:mounts"));
        resource.add(linkTo(methodOn(ContainerDependencyController.class).getDependencies(name))
                .withRel("cu:dependencies"));

        if (container.getState() == ContainerState.STOPPED) {
            resource.add(linkTo(methodOn(ContainerController.class).start(name))
                    .withRel("cu:start"));
        }
        
        if (container.getState() == ContainerState.STARTED) {
            resource.add(linkTo(methodOn(ContainerController.class).stop(name))
                    .withRel("cu:stop"));
        }
        
        return resource;
    }
}

package fr.treeptik.cloudunit.orchestrator.resource;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class ContainerDependencyResource extends ResourceSupport {
    public ContainerDependencyResource() {}
    
    public ContainerDependencyResource(String containerUri) {
        add(new Link(containerUri, "cu:dependency"));
    }
}

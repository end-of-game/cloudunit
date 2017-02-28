package fr.treeptik.cloudunit.orchestrator.resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

public class ContainerResource extends ResourceSupport {
    private String containerId;
    
    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9][a-zA-Z0-9_.-]*")
    private String name;

    @NotNull
    @Pattern(regexp = "([a-z]+/)?[a-z]([a-z0-9_.-])*(:[a-z0-9]([a-z0-9_.-])*)?")
    private String imageName;
    
    private ContainerState state;

    public ContainerResource() {}
    
    public ContainerResource(Container container) {
        this.containerId = container.getId();
        this.name = container.getName();
        this.imageName = container.getImageName();
        this.state = container.getState();
    }

    public ContainerResource(String name, String imageName) {
        this.name = name;
        this.imageName = imageName;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    public ContainerState getState() {
        return state;
    }
    
    public void setState(ContainerState state) {
        this.state = state;
    }

}

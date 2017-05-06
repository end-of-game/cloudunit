package fr.treeptik.cloudunit.domain.resource;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

@Relation(value = "cu:service", collectionRelation = "cu:services")
public class ServiceResource extends ResourceSupport {
    private String name;
    private String imageName;
    private ContainerState state;
    
    protected ServiceResource() {}
    
    public ServiceResource(Service service) {
        this.name = service.getName();
        this.imageName = service.getImageName();
        this.state = service.getState();
    }

    public ServiceResource(String imageName) {
        this.imageName = imageName;
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

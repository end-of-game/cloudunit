package fr.treeptik.cloudunit.domain.resource;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.domain.core.Service;

public class ServiceResource extends ResourceSupport {
    private String imageName;
    
    protected ServiceResource() {}
    
    public ServiceResource(Service service) {
        this.imageName = service.getImageName();
    }

    public ServiceResource(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

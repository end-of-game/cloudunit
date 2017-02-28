package fr.treeptik.cloudunit.orchestrator.resource;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;

public class ImageResource extends ResourceSupport {
    private String name;
    private ImageType type;

    public ImageResource() {}
    
    public ImageResource(Image image) {
        this.name = image.getName();
        this.type = image.getType();
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ImageType getType() {
        return type;
    }
    
    public void setType(ImageType type) {
        this.type = type;
    }
}

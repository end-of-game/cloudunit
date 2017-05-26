package fr.treeptik.cloudunit.orchestrator.resource;

import org.springframework.hateoas.ResourceSupport;


import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.core.ImageType;
import org.springframework.hateoas.core.Relation;

@Relation(value = "cu:image", collectionRelation = "cu:images")
public class ImageResource extends ResourceSupport {
    private String name;
    private ImageType type;
    private String displayName;
    private String serviceName;
    private String version;

    public ImageResource() {}
    
    public ImageResource(Image image) {
        this.name = image.getName();
        this.type = image.getType();
        this.displayName = image.getDisplayName();
        this.serviceName = image.getServiceName();
        this.version = image.getVersion();
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

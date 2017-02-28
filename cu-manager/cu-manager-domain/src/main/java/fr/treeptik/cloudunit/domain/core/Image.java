package fr.treeptik.cloudunit.domain.core;

import fr.treeptik.cloudunit.orchestrator.core.ImageType;

public class Image {
    private String id;
    private String name;
    private ImageType type;
    
    protected Image() {}
    
    public Image(String name, ImageType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getBasename() {
        if (name.contains("/")) {
            return name.split("/", 2)[1];
        } else {
            return name;
        }
    }
    
    public ImageType getType() {
        return type;
    }
}

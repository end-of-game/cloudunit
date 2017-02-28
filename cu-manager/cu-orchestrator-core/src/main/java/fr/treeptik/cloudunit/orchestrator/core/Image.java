package fr.treeptik.cloudunit.orchestrator.core;

public class Image {
    private String id;
    private String name;
    private ImageType type;
    
    public Image() {}
    
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
    
    public ImageType getType() {
        return type;
    }
}

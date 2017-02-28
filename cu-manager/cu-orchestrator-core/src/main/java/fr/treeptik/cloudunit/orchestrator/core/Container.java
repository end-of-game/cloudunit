package fr.treeptik.cloudunit.orchestrator.core;

public class Container {
    private String id;
    private String name;
    private String imageName;
    private ContainerState state;
    
    protected Container() {}
    
    public Container(String name, Image image) {
        this.name = name;
        this.imageName = image.getName();
        this.state = ContainerState.CREATED;
    }

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public ContainerState getState() {
        return state;
    }
    
    public String getImageName() {
        return imageName;
    }
}

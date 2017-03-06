package fr.treeptik.cloudunit.orchestrator.core;

public class Container {
    private String id;
    private String containerId;
    private String name;
    private String imageName;
    private ContainerState state;
    
    protected Container() {}
    
    public Container(String name, Image image) {
        this.name = name;
        this.imageName = image.getName();
        this.state = ContainerState.STOPPED;
    }

    public String getId() {
        return id;
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
    
    public ContainerState getState() {
        return state;
    }
    
    public void setState(ContainerState state) {
        this.state = state;
    }
    
    public String getImageName() {
        return imageName;
    }
}

package fr.treeptik.cloudunit.orchestrator.resource;

public class ContainerEvent {
    public enum Type {
        CREATED,
        DELETED,
        CHANGED,
        DEPLOYED;
    }
    
    private Type type;
    
    private ContainerResource container;
    
    public ContainerEvent() {}
    
    public ContainerEvent(Type type, ContainerResource container) {
        this.type = type;
        this.container = container;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ContainerResource getContainer() {
        return container;
    }

    public void setContainer(ContainerResource container) {
        this.container = container;
    }
}

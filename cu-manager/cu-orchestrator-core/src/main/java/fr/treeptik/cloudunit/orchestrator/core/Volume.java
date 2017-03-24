package fr.treeptik.cloudunit.orchestrator.core;

public class Volume {
    private String id;
    private String name;
    
    public Volume() {}
    
    public Volume(String name) {
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}

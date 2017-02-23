package fr.treeptik.cloudunit.domain.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Application {
    private String id;
    private String name;
    private String displayName;
    
    protected Application() {}
    
    public Application(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

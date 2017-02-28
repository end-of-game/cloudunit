package fr.treeptik.cloudunit.domain.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Application {
    private String id;
    private String name;
    private String displayName;
    private Map<String, Service> services;
    
    protected Application() {}
    
    public Application(String name) {
        this.name = name;
        this.services = new HashMap<>();
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

    public Service addService(Image image) {
        Service service = Service.of(this, image);
        services.put(service.getName(), service);
        return service;
    }

    public Collection<Service> getServices() {
        return Collections.unmodifiableCollection(services.values());
    }

    public Optional<Service> getService(String name) {
        return Optional.ofNullable(services.get(name));
    }

    public void removeService(String name) {
        services.remove(name);
    }
}

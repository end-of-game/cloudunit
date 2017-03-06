package fr.treeptik.cloudunit.domain.core;

import static fr.treeptik.cloudunit.domain.core.ApplicationState.*;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Application {
    private String id;
    private String name;
    private String displayName;
    private AtomicReference<ApplicationState> state;
    private Map<String, Service> servicesByName;
    private Map<String, String> serviceNamesByContainerName;
    
    protected Application() {}
    
    public Application(String name) {
        this.name = name;
        this.servicesByName = new HashMap<>();
        this.serviceNamesByContainerName = new HashMap<>();
        this.state = new AtomicReference<>(CREATED);
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
    
    public ApplicationState getState() {
        return state.get();
    }
    
    /**
     * Transition to a new state if possible.
     * 
     * @return {@code true} if the transition was allowed; false otherwise
     */
    private boolean transition(Set<ApplicationState> requiredStates, ApplicationState targetState) {
        return requiredStates.stream()
                .anyMatch(required -> state.compareAndSet(required, targetState)); // ! side effect }:-)
    }
    
    private boolean transition(ApplicationState requiredState, ApplicationState targetState) {
        return transition(EnumSet.of(requiredState), targetState);
    }
    
    /**
     * Transition to {@linkplain ApplicationState#STARTING starting} state if possible.
     * 
     * @return {@code true} if this application can be started; false otherwise
     */
    public boolean start() {
        return transition(EnumSet.of(CREATED, STOPPED), STARTING);
    }
    
    /**
     * Transition to {@linkplain ApplicationState#STARTED started} state if possible.
     * 
     * @return {@code true} if this application can be started; false otherwise
     */
    public boolean started() {
        return transition(STARTING, STARTED);
    }
    
    /**
     * Transition to {@linkplain ApplicationState#STOPPED stopped} state if possible.
     * 
     * @return {@code true} if this application can be stopped; false otherwise
     */
    public boolean stop() {
        return transition(STARTED, STOPPING);
    }
    
    public boolean stopped() {
        return transition(STOPPING, STOPPED);
    }
    
    public boolean remove() {
        return transition(EnumSet.of(STARTED, STOPPED), REMOVING);
    }
    
    public boolean isPending() {
        return state.get().isPending();
    }
    
    public Service addService(Image image) {
        Service service = Service.of(this, image);
        servicesByName.put(service.getName(), service);
        serviceNamesByContainerName.put(service.getContainerName(), service.getName());
        return service;
    }

    public Collection<Service> getServices() {
        return Collections.unmodifiableCollection(servicesByName.values());
    }

    public Optional<Service> getService(String name) {
        return Optional.ofNullable(servicesByName.get(name));
    }

    public void removeService(String name) {
        servicesByName.remove(name);
        serviceNamesByContainerName.remove(name);
    }

    public Service getServiceByContainerName(String containerName) {
        return servicesByName.get(serviceNamesByContainerName.get(containerName));
    }
}

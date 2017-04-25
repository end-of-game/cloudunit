package fr.treeptik.cloudunit.domain.core;

import static fr.treeptik.cloudunit.domain.core.ApplicationState.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Application {
    private static final EnumSet<ApplicationState> STARTED_STOPPED = EnumSet.of(STARTED, STOPPED);
    private String id;
    private String name;
    private String displayName;
    private AtomicReference<ApplicationState> state;
    private List<Service> services;
    
    @Version
    private Long version;
    
    protected Application() {}
    
    public Application(String name) {
        this.name = name;
        this.services = new ArrayList<>();
        this.state = new AtomicReference<>(STOPPED);
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
    
    /**
     * Transition to a new state if possible.
     * 
     * @return {@code true} if the transition was allowed; false otherwise
     */
    private boolean transition(ApplicationState requiredState, ApplicationState targetState) {
        return transition(EnumSet.of(requiredState), targetState);
    }
    
    public boolean pending() {
        return transition(STOPPED, STOPPING) || transition(STARTED, STARTING);
    }
    
    /**
     * Transition to {@linkplain ApplicationState#STARTING starting} state if possible.
     * 
     * @return {@code true} if this application can be started; false otherwise
     */
    public boolean start() {
        return transition(STARTED_STOPPED, STARTING);
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
     * Transition to {@linkplain ApplicationState#STOPPED stopping} state if possible.
     * 
     * @return {@code true} if this application can be stopping; false otherwise
     */
    public boolean stop() {
        return transition(STARTED_STOPPED, STOPPING);
    }
    
    /**
     * Transition to {@linkplain ApplicationState#STOPPED stopped} state if possible.
     * 
     * @return {@code true} if this application can be stopped; false otherwise
     */
    public boolean stopped() {
        return transition(STOPPING, STOPPED);
    }
    
    public boolean remove() {
        return transition(STARTED_STOPPED, REMOVING);
    }
    
    public boolean isPending() {
        return state.get().isPending();
    }
    
    public Service addService(Image image) {
        Service service = Service.of(this, image);
        services.add(service);
        return service;
    }

    public Collection<Service> getServices() {
        return Collections.unmodifiableCollection(services);
    }

    public Optional<Service> getService(String name) {
        return services.stream()
                .filter(s -> s.getName().equals(name))
                .findAny();
    }

    public void removeService(String name) {
        getService(name).ifPresent(services::remove);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Application)) return false;
        
        Application other = (Application) obj;
        return new EqualsBuilder()
                .append(this.name, other.name)
                .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

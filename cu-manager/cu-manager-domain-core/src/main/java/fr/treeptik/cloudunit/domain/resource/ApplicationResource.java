package fr.treeptik.cloudunit.domain.resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.ApplicationState;

public class ApplicationResource extends ResourceSupport {
    @NotNull
    @Pattern(regexp = "[a-z]([0-9a-z-]*[0-9a-z])?")
    private String name;
    
    private ApplicationState state;
    
    private boolean pending;
    
    public ApplicationResource() {}
    
    public ApplicationResource(Application application) {
        this.name = application.getName();
        this.state = application.getState();
        this.pending = application.isPending();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ApplicationState getState() {
        return state;
    }
    
    public void setState(ApplicationState state) {
        this.state = state;
    }
    
    public boolean isPending() {
        return pending;
    }
    
    public void setPending(boolean pending) {
        this.pending = pending;
    }
}

package fr.treeptik.cloudunit.domain.resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.domain.model.Application;

public class ApplicationResource extends ResourceSupport {
    @NotNull
    @Pattern(regexp = "[a-z]([0-9a-z-]*[0-9a-z])?")
    private String name;
    
    public ApplicationResource() {}
    
    public ApplicationResource(Application application) {
        this.name = application.getName();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}

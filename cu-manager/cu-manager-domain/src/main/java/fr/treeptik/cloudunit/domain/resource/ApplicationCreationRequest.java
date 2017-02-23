package fr.treeptik.cloudunit.domain.resource;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class ApplicationCreationRequest {
    @NotNull
    @Pattern(regexp = "[a-z]([a-z-]*[a-z])?")
    private String name;
    
    @NotNull
    @Pattern(regexp = "[a-z]([a-z-]*[a-z])?")
    private String imageName;
    
    public ApplicationCreationRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

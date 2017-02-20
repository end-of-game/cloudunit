package fr.treeptik.cloudunit.dto;

import java.util.Arrays;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.treeptik.cloudunit.enums.JvmMemory;
import fr.treeptik.cloudunit.model.Server;

public class ServerResource extends ResourceSupport {
    @NotNull(groups = Full.class)
    private Long jvmMemory;
    
    @Pattern(regexp = "^[^-]*(-(?!Xms)[^-]*)*$", groups = Patch.class)
    private String jvmOptions;
    
    private ImageResource image;

    public ServerResource() {}
    
    public ServerResource(Server server) {
        this.jvmMemory = server.getJvmMemory();
        this.jvmOptions = server.getJvmOptions();
        
        this.image = new ImageResource(server.getImage());
    }

    public Long getJvmMemory() {
        return jvmMemory;
    }

    public void setJvmMemory(Long jvmMemory) {
        this.jvmMemory = jvmMemory;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }
    
    public ImageResource getImage() {
        return image;
    }
    
    public void setImage(ImageResource image) {
        this.image = image;
    }

    public void patch(Server server) {
        if (jvmMemory != null) {
            server.setJvmMemory(jvmMemory);
        }
        
        if (jvmOptions != null) {
            server.setJvmOptions(jvmOptions);
        }
    }

    @AssertTrue(message = "Invalid memory size", groups = Patch.class)
    @JsonIgnore
    public boolean isValidMemory() {
        return jvmMemory == null
                || Arrays.stream(JvmMemory.values())
                    .filter(v -> v.getSize().equals(jvmMemory.toString()))
                    .findAny()
                    .isPresent();
    }
    
    public void put(Server server) {
        server.setJvmMemory(jvmMemory);
        server.setJvmOptions(jvmOptions);
    }
    
    /**
     * Bean validation group for full request validation.
     */
    public interface Full extends Patch {}
    
    /**
     * Bean validation group for validating only specified fields.
     */
    public interface Patch {}
}

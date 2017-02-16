package fr.treeptik.cloudunit.dto;

import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;

public class ApplicationResource extends ResourceSupport {
    @Pattern(regexp = "^[a-z][a-z0-9-]*[a-z0-9]$")
    private String name;
    
    @Pattern(regexp = ".*\\S.*")
    private String displayName;

    @NotNull
    @Pattern(regexp = "^[a-z][a-z0-9-]*[a-z0-9]$")
    private String serverType;
    
    private Status status;
    
    @JsonFormat(pattern = "YYYY-MM-ddTHH:mm:ss")
    private Date creationDate;
    
    public ApplicationResource() {}
    
    public ApplicationResource(Application application) {
        this.name = application.getName();
        this.displayName = application.getDisplayName();
        this.status = application.getStatus();
        this.creationDate = application.getDate();
        this.serverType = application.getServer().getImage().getName();
    }
    
    @JsonIgnore
    @AssertTrue
    public boolean isNameOrDisplayNameSet() {
        return name != null || displayName != null;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
        
    public String getServerType() {
        return serverType;
    }
    
    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Date getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}

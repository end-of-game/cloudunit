package fr.treeptik.cloudunit.dto;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.treeptik.cloudunit.model.Deployment;
import fr.treeptik.cloudunit.model.DeploymentType;

public class DeploymentResource extends ResourceSupport {
    private DeploymentType type;
    private String contextPath;
    private String filename;
    
    @JsonFormat(pattern = "YYYY-MM-ddThh:mm:ss")
    private Date date;
    
    public DeploymentResource() {}
    
    public DeploymentResource(Deployment deployment) {
        this.type = deployment.getType();
        this.contextPath = deployment.getContextPath();
        this.filename = deployment.getFilename();
        this.date = deployment.getDate();
    }
    
    public DeploymentType getType() {
        return type;
    }
    
    public void setType(DeploymentType type) {
        this.type = type;
    }
    
    public String getContextPath() {
        return contextPath;
    }
    
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
}

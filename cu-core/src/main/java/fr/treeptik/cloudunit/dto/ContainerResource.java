package fr.treeptik.cloudunit.dto;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.model.Container;
import fr.treeptik.cloudunit.model.Status;

public class ContainerResource extends ResourceSupport {

    private String containerId;
    
    private String name;
    
    private Status status;

    private Date startDate;

    public ContainerResource() {}
    
    public ContainerResource(Container container) {
        this.containerId = container.getContainerID();
        this.name = container.getName();
        this.status = container.getStatus();
        this.startDate = container.getStartDate();
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}

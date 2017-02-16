package fr.treeptik.cloudunit.dto;

import javax.validation.constraints.NotNull;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.model.Port;

public class PortResource extends ResourceSupport {
    @NotNull
	private Boolean open;
    
    private String hostValue;
	
	/**
	 * @deprecated only to be used by Jackson deserialization.
	 */
	@Deprecated
	protected PortResource() {}
	
	public PortResource(Port port) {
		this.open = port.getOpened();
		this.hostValue = port.getHostValue();
	}
	
	public PortResource(boolean open) {
        this.open = open;
    }

    public Boolean getOpen() {
        return open;
    }
	
	public void setOpen(Boolean open) {
        this.open = open;
    }
	
	public String getHostValue() {
        return hostValue;
    }
	
	public void setHostValue(String hostValue) {
        this.hostValue = hostValue;
    }
}

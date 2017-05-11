package fr.treeptik.cloudunit.domain.resource;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

public class ServiceResource extends ResourceSupport {
	private String imageName;
	private String containerName;
	private ContainerState state;

	protected ServiceResource() {
	}

	public ServiceResource(Service service) {
		this.imageName = service.getImageName();
		this.containerName = service.getContainerName();
		this.state = service.getState();
	}

	public ServiceResource(String imageName) {
		this.imageName = imageName;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public ContainerState getState() {
		return state;
	}

	public void setState(ContainerState state) {
		this.state = state;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}

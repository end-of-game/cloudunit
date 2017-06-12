package fr.treeptik.cloudunit.domain.resource;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.orchestrator.core.ContainerState;

@Relation(value = "cu:service", collectionRelation = "cu:services")
public class ServiceResource extends ResourceSupport {
	private String name;
	private String imageName;
	private String containerName;
	private ContainerState state;

	protected ServiceResource() {
	}

	private ServiceResource(Builder builder) {
		this.name = builder.name;
		this.imageName = builder.imageName;
		this.containerName = builder.containerName;
		this.state = builder.state;
	}

	public ServiceResource(Service service) {
		this.imageName = service.getImageName();
		this.containerName = service.getContainerName();
		this.state = service.getState();
		this.name = service.getName();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static Builder of() {
		return new Builder();
	}

	public static class Builder {
		private String name;
		private String imageName;
		private String containerName;
		private ContainerState state;

		private Builder() {}

		public String name() {
			return name;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public String imageName() {
			return imageName;
		}

		public Builder imageName(String imageName) {
			this.imageName = imageName;
			return this;
		}

		public String containerName() {
			return containerName;
		}

		public Builder containerName(String containerName) {
			this.containerName = containerName;
			return this;
		}

		public ContainerState state() {
			return state;
		}

		public Builder state(ContainerState state) {
			this.state = state;
			return this;
		}

		public ServiceResource build() {
			return new ServiceResource(this);
		}
	}
}

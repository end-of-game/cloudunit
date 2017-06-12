package fr.treeptik.cloudunit.domain.resource;

public class DeploymentEvent {
	public enum Type {
		DEPLOYED;
	}

	private Type type;

	private ApplicationResource application;
	private ServiceResource service;
	private DeploymentResource deployment;

	public DeploymentEvent() {
	}

	public DeploymentEvent(Type type, ApplicationResource application, ServiceResource service, DeploymentResource deployment) {
		this.type = type;
		this.application = application;
		this.service = service;
		this.deployment = deployment;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public DeploymentResource getDeployment() {	return deployment; }

	public void setDeployment(DeploymentResource deployment) { this.deployment = deployment; }

	public ServiceResource getService() { return service; }

	public void setService(ServiceResource service) { this.service = service; }
}

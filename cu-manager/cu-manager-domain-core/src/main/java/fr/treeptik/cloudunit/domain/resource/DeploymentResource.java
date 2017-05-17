package fr.treeptik.cloudunit.domain.resource;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.domain.core.Deployment;

public class DeploymentResource extends ResourceSupport {

	private String contextPath;
	
	protected DeploymentResource() {
	}

	public DeploymentResource(Deployment deployment) {
		this.contextPath = deployment.getContextPath();
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

}

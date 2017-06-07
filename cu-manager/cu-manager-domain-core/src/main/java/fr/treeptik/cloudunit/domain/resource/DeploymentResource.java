package fr.treeptik.cloudunit.domain.resource;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import fr.treeptik.cloudunit.domain.core.Deployment;

@Relation(value = "cu:deployment", collectionRelation = "cu:deployments")
public class DeploymentResource extends ResourceSupport {

	private String contextPath;
	private String fileUri;
	
	protected DeploymentResource() {
	}

	public DeploymentResource(String contextPath, String fileUri) {
		this.contextPath = contextPath;
		this.fileUri = fileUri;
	}

	public DeploymentResource(Deployment deployment) {
		this.contextPath = deployment.getContextPath();
		this.fileUri = deployment.getFileUri();
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getFileUri() { return fileUri; }

	public void setFileUri(String fileUri) { this.fileUri = fileUri; }

}


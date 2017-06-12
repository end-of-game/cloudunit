package fr.treeptik.cloudunit.domain.core;

public class Deployment {

	private String contextPath;
	private String fileUri;

	protected Deployment() {
	}

	public Deployment(String contextPath, String fileUri) {
		this.contextPath = contextPath;
		this.fileUri = fileUri;
	}

	public String getFileUri() { return fileUri; }

	public void setFileUri(String fileUri) { this.fileUri = fileUri; }

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}


}

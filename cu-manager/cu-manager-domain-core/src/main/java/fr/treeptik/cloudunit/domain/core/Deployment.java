package fr.treeptik.cloudunit.domain.core;

public class Deployment {

	private String contextPath;

	protected Deployment() {
	}

	public Deployment(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

}

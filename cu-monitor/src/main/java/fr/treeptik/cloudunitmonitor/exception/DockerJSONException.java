package fr.treeptik.cloudunitmonitor.exception;

public class DockerJSONException extends Exception {

	private static final long serialVersionUID = 1L;

	public DockerJSONException(String message, Throwable e) {
		super(message, e);
	}

	public DockerJSONException(String message) {
		super(message);
	}

}

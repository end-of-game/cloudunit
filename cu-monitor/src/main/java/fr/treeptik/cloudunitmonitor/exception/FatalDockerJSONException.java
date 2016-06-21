package fr.treeptik.cloudunitmonitor.exception;

public class FatalDockerJSONException extends DockerJSONException {

	private static final long serialVersionUID = 1L;

	public FatalDockerJSONException(String message) {
		super(message);
	}

}

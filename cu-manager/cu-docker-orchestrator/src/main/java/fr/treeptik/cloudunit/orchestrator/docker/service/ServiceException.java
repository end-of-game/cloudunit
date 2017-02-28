package fr.treeptik.cloudunit.orchestrator.docker.service;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = -3900132012494618956L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}

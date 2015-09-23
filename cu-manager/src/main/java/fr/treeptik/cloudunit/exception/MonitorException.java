package fr.treeptik.cloudunit.exception;

public class MonitorException extends Exception {

	private static final long serialVersionUID = 1L;

	public MonitorException(String message, Throwable e) {
		super(message, e);
	}
}

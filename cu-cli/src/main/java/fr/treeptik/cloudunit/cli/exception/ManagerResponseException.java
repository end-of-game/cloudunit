package fr.treeptik.cloudunit.cli.exception;

/**
 * Created by guillaume on 16/10/15.
 */
public class ManagerResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	public ManagerResponseException(String message, Throwable e) {
		super(message, e);
	}
}

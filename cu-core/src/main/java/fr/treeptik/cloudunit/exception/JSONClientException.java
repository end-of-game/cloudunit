package fr.treeptik.cloudunit.exception;

/**
 * Created by guillaume on 21/10/15.
 */
public class JSONClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public JSONClientException(String message, Throwable e) {
		super(message, e);
	}
}

package fr.treeptik.cloudunit.exception;

public class ProviderException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProviderException(String message, Throwable e) {
		super(message, e);
	}
}

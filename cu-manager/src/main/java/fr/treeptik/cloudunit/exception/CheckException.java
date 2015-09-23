package fr.treeptik.cloudunit.exception;


/**
 * Exception throwing after a client bad request.
 * Code error 4xx
 */
public class CheckException extends Exception {

	private static final long serialVersionUID = 1L;

	public CheckException() {
	}
	
	public CheckException(String message) {
		super(message);
	}
	
	public CheckException(Throwable e) {  
		super(e); 
	}
	
	public CheckException(String message, Throwable e) {
		super(message, e);
	}
}

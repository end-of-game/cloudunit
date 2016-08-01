package fr.treeptik.cloudunit.exception;

/**
 * Created by guillaume on 21/10/15.
 */
public class JSONClientException extends Exception {

    public JSONClientException(String message, Throwable e) {
        super(message, e);
    }
}

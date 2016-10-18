package fr.treeptik.cloudunit.cli;

/**
 * Created by nicolas on 10/10/2016.
 */
public class CloudUnitCliException extends RuntimeException {
    private static final long serialVersionUID = -1425403407922027331L;

    public CloudUnitCliException() {
    }

    public CloudUnitCliException(String message) {
        super(message);
    }

    public CloudUnitCliException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudUnitCliException(Throwable cause) {
        super(cause);
    }

    public CloudUnitCliException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

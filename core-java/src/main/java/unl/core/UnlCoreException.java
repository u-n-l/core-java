package unl.core;

public class UnlCoreException extends Exception {
    public UnlCoreException(String errorMessage, Throwable t) {
        super(errorMessage, t);
    }

    public UnlCoreException(String errorMessage) {
        super(errorMessage);
    }
}

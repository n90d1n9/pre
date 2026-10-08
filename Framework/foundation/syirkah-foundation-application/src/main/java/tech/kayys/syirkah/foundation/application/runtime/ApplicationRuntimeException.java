package tech.kayys.syirkah.foundation.application.runtime;

/**
 * Root exception for runtime lifecycle and execution errors.
 */
public class ApplicationRuntimeException extends RuntimeException {

    public ApplicationRuntimeException(String message) {
        super(message);
    }

    public ApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

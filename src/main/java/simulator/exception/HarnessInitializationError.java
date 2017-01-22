package simulator.exception;

/**
 * Created by adam on 1/21/17.
 */
public class HarnessInitializationError extends RuntimeException {
    public HarnessInitializationError(final Throwable cause) {
        super(cause);
    }
}

package simulator.exception;

/**
 * RuntimeException indicating an exception occurred during simulation termination.
 */
public class SimulationTerminationError extends RuntimeException {
    public SimulationTerminationError(final Throwable cause) {
        super(cause);
    }
}

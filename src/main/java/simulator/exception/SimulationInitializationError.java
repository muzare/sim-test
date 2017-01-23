package simulator.exception;

/**
 * RuntimeException indicating an error occurred while initializing the exchange simulator process.
 */
public class SimulationInitializationError extends RuntimeException {
    public SimulationInitializationError(final Throwable cause) {
        super(cause);
    }
}

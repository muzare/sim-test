package simulator.exception;

/**
 * RuntimeException indicating that an exception occurred while writing to the simulator process.
 */
public class SimulatorInputError extends RuntimeException {
    public SimulatorInputError(final Throwable cause) {
        super(cause);
    }
}

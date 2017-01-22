package simulator.exception;

/**
 * Created by adam on 1/22/17.
 */
public class SimulationTerminationError extends RuntimeException {
    public SimulationTerminationError(final Throwable cause) {
        super(cause);
    }
}

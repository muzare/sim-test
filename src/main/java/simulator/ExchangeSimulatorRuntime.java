package simulator;

import org.junit.rules.ExternalResource;

/**
 * ExternalResource for tests running the {@link ExchangeSimulator}. Guarantees proper initialization and cleanup of the simulation.
 */
public final class ExchangeSimulatorRuntime extends ExternalResource {

    /**
     * Constructs a new {@link ExchangeSimulatorRuntime}.
     */
    public ExchangeSimulatorRuntime() {
    }

    private ExchangeSimulator simulator;

    /**
     * @inheritDoc
     */
    @Override
    protected void before() throws Throwable {
        super.before();
        simulator = ExchangeSimulator.createSimulation();
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void after() {
        try {
            super.after();
        } finally {
            simulator.endSimulation();
        }
    }

    /**
     * Accesses the running simulator.
     *
     * @return Non-null {@link ExchangeSimulator} for this runtime.
     */
    public ExchangeSimulator getSimulator() {
        return simulator;
    }
}

package simulator;

import org.joda.time.Seconds;
import org.junit.rules.ExternalResource;

/**
 * Created by adam on 1/21/17.
 */
public final class ExchangeSimulatorRuntime extends ExternalResource {

    private final Seconds processingGracePeriod;

    public ExchangeSimulatorRuntime(final Seconds processingGracePeriod) {
        this.processingGracePeriod = processingGracePeriod;
    }

    private ExchangeSimulator simulator;

    @Override
    protected void before() throws Throwable {
        super.before();
        simulator = ExchangeSimulator.createSimulation(processingGracePeriod);
    }

    @Override
    protected void after() {
        try {
            super.after();
        } finally {
            simulator.endSimulation();
        }
    }

    public ExchangeSimulator getSimulator() {
        return simulator;
    }
}

package simulator;

import com.google.common.collect.ImmutableList;
import simulator.output.Trade;

/**
 * Created by adam on 1/21/17.
 */
public class SimulationResults {

    private final ImmutableList<Trade> orders;
    private final ImmutableList<String> warnings;
    private final boolean processAborted;

    public SimulationResults(final ImmutableList<Trade> orders, final ImmutableList<String> warnings) {
        this.orders = orders;
        this.warnings = warnings;
        this.processAborted = false;
    }

    public SimulationResults(final ImmutableList<Trade> orders, final ImmutableList<String> warnings, final boolean processAborted) {
        this.orders = orders;
        this.warnings = warnings;
        this.processAborted = processAborted;
    }

    public ImmutableList<Trade> getTrades() {
        return orders;
    }

    public ImmutableList<String> getWarnings() {
        return warnings;
    }

    public boolean isProcessAborted() {
        return processAborted;
    }
}

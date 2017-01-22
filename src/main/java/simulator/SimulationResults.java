package simulator;

import com.google.common.collect.ImmutableList;
import simulator.output.Trade;

/**
 * Created by adam on 1/21/17.
 */
public class SimulationResults {

    private final ImmutableList<Trade> orders;
    private final ImmutableList<String> warnings;

    public SimulationResults(final ImmutableList<Trade> orders, final ImmutableList<String> warnings) {
        this.orders = orders;
        this.warnings = warnings;
    }

    public ImmutableList<Trade> getOrders() {
        return orders;
    }

    public ImmutableList<String> getWarnings() {
        return warnings;
    }
}

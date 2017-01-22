package simulator;

import com.google.common.collect.ImmutableList;
import simulator.output.IgnoredInputWarning;
import simulator.output.Trade;

/**
 * Created by adam on 1/21/17.
 */
public class TransactionResults {

    private final ImmutableList<Trade> orders;
    private final ImmutableList<IgnoredInputWarning> ignoredInputWarnings;

    public TransactionResults(final ImmutableList<Trade> orders, final ImmutableList<IgnoredInputWarning> ignoredInputWarnings) {
        this.orders = orders;
        this.ignoredInputWarnings = ignoredInputWarnings;
    }

    public ImmutableList<Trade> getOrders() {
        return orders;
    }

    public ImmutableList<IgnoredInputWarning> getIgnoredInputWarnings() {
        return ignoredInputWarnings;
    }
}

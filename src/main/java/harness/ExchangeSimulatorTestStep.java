package harness;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import simulator.SimulationResults;
import simulator.input.Order;
import simulator.output.Trade;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

/**
 * Represents a test step for the {@link simulator.ExchangeSimulator}. Extending classes should re=implement the members of this class to provide the expected input
 * to the similar and the appropriate assertions on the output.
 */
public abstract class ExchangeSimulatorTestStep {

    private final ExchangeSimulatorTestHarness exchangeSimulatorTestHarness;

    /**
     * Constructs a new {@link ExchangeSimulatorTestStep}
     *
     * @param exchangeSimulatorTestHarness The {@link ExchangeSimulatorTestHarness} for the test class (cannot be null).
     * @throws IllegalArgumentException if the parameter conditions are not met.
     */
    public ExchangeSimulatorTestStep(final ExchangeSimulatorTestHarness exchangeSimulatorTestHarness) {
        Preconditions.checkArgument(exchangeSimulatorTestHarness != null, "Cannot instantiate a test step with a null test harness.");
        this.exchangeSimulatorTestHarness = exchangeSimulatorTestHarness;
    }

    /**
     * Extending classes should implement this to populate the {@link Order} objects in the sequence that they should be provided to the simulator.
     *
     * @param orderListBuilder A non-null ImmutableList.Builder to populate.
     */
    protected abstract void populateOrders(final ImmutableList.Builder<Order> orderListBuilder);

    /**
     * Extending classes should implement this to return the Matchers for the expected {@link Trade}s output by the simulator. By default, no trades are expected.
     *
     * @return Matcher for the expected {@link Trade}s output by the simulator.
     */
    protected Matcher<Trade>[] getTradeMatchers() {
        return new Matcher[]{};
    }

    /**
     * Extending classes should implement this to return the Matchers for the expected warnings output by the simulator. By default, no warnings are expected.
     *
     * @return Matcher for the expected {warningss output by the simulator.
     */
    protected Matcher<String>[] getWarningMatchers() {
        return new Matcher[]{};
    }

    /**
     * Extending classes should implement this to indicate whether the process is expected to abort upon receiving the input specified in {@link #populateOrders(ImmutableList.Builder)}.
     * By default, the process is not expected to abort.
     *
     * @return True if the simulation should abort after receiving the input Orders, false otherwise.
     */
    protected boolean shouldProcessAbort() {
        return false;
    }

    /**
     * Performs the test step, first sending the orders populated via {@link #populateOrders(ImmutableList.Builder)} to the {@link simulator.ExchangeSimulator}, then asserting the
     * {@link SimulationResults} according to the test step implementation.
     */
    protected final void performStep() {
        final ImmutableList.Builder orderListBuilder = ImmutableList.builder();
        populateOrders(orderListBuilder);

        final SimulationResults simulationResults = exchangeSimulatorTestHarness.simulatorRuntime.getSimulator().processOrders(orderListBuilder.build());

        final Matcher<Trade>[] tradeMatchers = getTradeMatchers();
        if (tradeMatchers.length == 0) {
            assertThat("No trades expected", simulationResults.getTrades(), empty());
        } else {
            assertThat("Expected trades not present or not ordered as expected", simulationResults.getTrades(), contains(tradeMatchers));
        }

        final Matcher<String>[] warningMatchers = getWarningMatchers();
        if (warningMatchers.length == 0) {
            assertThat("No warnings expected", simulationResults.getWarnings(), empty());
        } else {
            assertThat("Expected warnings not present or not ordered as expected", simulationResults.getWarnings(), contains(warningMatchers));
        }

        assertThat("Process " + (shouldProcessAbort() ? "did not " : "was expected to ") + "abort", simulationResults.isProcessAborted(), is(shouldProcessAbort()));
    }
}

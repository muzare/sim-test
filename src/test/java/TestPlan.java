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
 * Created by adam on 1/22/17.
 */
abstract class TestPlan {
    private final ValidationTest validationTest;

    public TestPlan(final ValidationTest validationTest) {
        this.validationTest = validationTest;
    }

    protected abstract void populateOrders(final ImmutableList.Builder<Order> orderListBuilder);

    protected Matcher<Trade>[] getTradeMatchers() {
        return new Matcher[]{};
    }

    protected Matcher<String>[] getWarningMatchers() {
        return new Matcher[]{};
    }

    protected boolean shouldProcessAbort() {
        return false;
    }


    protected final void performTest() {
        final ImmutableList.Builder orderListBuilder = ImmutableList.builder();
        populateOrders(orderListBuilder);

        final SimulationResults simulationResults = validationTest.simulatorRuntime.getSimulator().processOrders(orderListBuilder.build());

        final Matcher<Trade>[] tradeMatchers = getTradeMatchers();
        if (tradeMatchers.length == 0) {
            assertThat("No trades expected", simulationResults.getOrders(), empty());
        } else {
            assertThat("Expected trades not present or not ordered as expected", simulationResults.getOrders(), contains(tradeMatchers));
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

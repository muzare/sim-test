import com.google.common.collect.ImmutableList;
import org.joda.time.Seconds;
import org.junit.Rule;
import org.junit.Test;
import simulator.ExchangeSimulatorRuntime;
import simulator.SimulationResults;
import simulator.input.Order;
import simulator.matcher.TradeMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;

/**
 * Created by adam on 1/21/17.
 */
public class ClientTest {

    @Rule
    public final ExchangeSimulatorRuntime simulatorRuntime = new ExchangeSimulatorRuntime(Seconds.ONE);

    @Test
    public void testTradeRestingOrders() {

        new TestPlan() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("SELL").withPrice(145.09).withQuantity(100).build());
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("BUY").withPrice(145.08).withQuantity(200).build());
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("BUY").withPrice(145.09).withQuantity(200).build());
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("SELL").withPrice(145.09).withQuantity(100).build());
            }

            protected void assertTrades(final SimulationResults simulationResults) {
                assertEquals(0, simulationResults.getWarnings().size());
                final TradeMatcher expectedTrade = new TradeMatcher("IBM", 145.09, 100);
                assertThat(simulationResults.getOrders(), contains(expectedTrade, expectedTrade));
            }
        }.performTest();
    }

    public abstract static class BaseTestPlan {
        protected enum Action {
            BUY, SELL
        }

        protected enum SYMBOLS {
            A, AB, IBM, IBMC, IBMCD, IBMCDE
        }
    }

    private abstract class TestPlan {
        protected abstract void populateOrders(final ImmutableList.Builder<Order> orderListBuilder);

        protected abstract void assertTrades(final SimulationResults simulationResults);

        protected final void performTest() {
            final ImmutableList.Builder orderListBuilder = ImmutableList.builder();
            populateOrders(orderListBuilder);
            assertTrades(simulatorRuntime.getSimulator().processOrders(orderListBuilder.build()));
        }
    }
}

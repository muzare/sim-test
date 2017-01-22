import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.junit.Test;
import simulator.input.Order;
import simulator.matcher.TradeMatcher;
import simulator.output.Trade;

/**
 * Created by adam on 1/21/17.
 */
public final class SimultaneousInputTradeTest extends ExchangeSimulatorTestHarness {

    /**
     * Verifies that, when a BUY order comes in, but no quantities are available, no trades are attempted.
     */
    public void noTradeOccursWithoutQuantity() {
        new SimultaneousInputTradeTestCase() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(createDefaultBuyOrder().withPrice(145.1).withQuantity(25).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        }.performTest();
    }

    /**
     * Verifies that, when a BUY order comes in, but no quantities are available, no trades are attempted.
     */
    public void noTradeOccursForMismatchingSymbols() {
        new SimultaneousInputTradeTestCase() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.1).withQuantity(25).build())
                        .add(createDefaultBuyOrder().withPrice(145.1).withQuantity(25).withSymbol("OMC").build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        }.performTest();
    }

    /**
     * Verifies that a SELL is traded when a BUY order comes in at a higher price.
     */
    @Test
    public void higherPricedBuyComesAfterSell() {
        new SimultaneousInputTradeTestCase() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(41.01).withQuantity(100).build())
                        .add(createDefaultBuyOrder().withPrice(41.02).withQuantity(100).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 41.01, 100)};
            }
        }.performTest();
    }

    /**
     * Verifies that a BUY order is traded when a subsequent SELL order comes in at a lower price.
     */
    @Test
    public void lowerPricedSellComesAfterBuy() {
        new SimultaneousInputTradeTestCase() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultBuyOrder().withPrice(41.02).withQuantity(200).build())
                        .add(createDefaultSellOrder().withPrice(41.01).withQuantity(100).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 41.02, 100)};
            }
        }.performTest();
    }

    /**
     * Verifies that, when a BUY order comes in that exceeds the owned capacity for a symbol, only the owned capacity is traded.
     */
    @Test
    public void buyQuantityExceedsCapacity() {
        new SimultaneousInputTradeTestCase() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.09).withQuantity(50).build())
                        .add(createDefaultBuyOrder().withPrice(145.1).withQuantity(200).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 145.09, 50)};
            }
        }.performTest();
    }

    /**
     * Verifies that, when a BUY order comes in with a lower quantity than the current capacity for trading, the trade occurs with the BUY quantity.
     */
    @Test
    public void buyQuantityLowerThanCapacity() {
        new SimultaneousInputTradeTestCase() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.09).withQuantity(50).build())
                        .add(createDefaultBuyOrder().withPrice(145.1).withQuantity(25).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 145.09, 25)};
            }
        }.performTest();
    }

    private abstract class SimultaneousInputTradeTestCase extends ExchangeSimulatorTestCase {

        /**
         * The default order symbol used in {@link #createDefaultSellOrder()} and {@link #createDefaultBuyOrder()}.
         */
        protected final String DEFAULT_SYMBOL = "IBM";

        /**
         * Constructs a new {@link SimultaneousInputTradeTest}.
         */
        public SimultaneousInputTradeTestCase() {
            super(SimultaneousInputTradeTest.this);
        }

        /**
         * @inheritDoc <p />
         * Overridden as abstract to force tests to implement. For these tests, the outputted trades are essential to the understanding of what the
         * test case is attempting to accomplish, so it's prudent that each test provide an explicit implementation.
         */
        @Override
        protected abstract Matcher<Trade>[] getTradeMatchers();

        /**
         * @return Non-null order for a SELL action with the {@value SimultaneousInputTradeTestCase##DEFAULT_SYMBOL}.
         */
        protected final Order.Builder createDefaultSellOrder() {
            return Order.Builder.create().withSymbol(DEFAULT_SYMBOL).withAction("SELL");
        }

        /**
         * @return Non-null order for a BUY action with the {@value SimultaneousInputTradeTestCase##DEFAULT_SYMBOL}.
         */
        protected final Order.Builder createDefaultBuyOrder() {
            return Order.Builder.create().withSymbol(DEFAULT_SYMBOL).withAction("BUY");
        }
    }
}

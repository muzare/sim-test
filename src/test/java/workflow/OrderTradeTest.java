package workflow;

import com.google.common.collect.ImmutableList;
import harness.ExchangeSimulatorTestHarness;
import org.hamcrest.Matcher;
import org.junit.Test;
import simulator.input.Order;
import simulator.matcher.TradeMatcher;
import simulator.output.Trade;

/**
 * Created by adam on 1/21/17.
 */
public final class OrderTradeTest extends ExchangeSimulatorTestHarness {

    /**
     * Verifies that, when a BUY order comes in, but no quantities are available, no trades are attempted.
     */
    @Test
    public void noTradeOccursWithoutQuantity() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(createDefaultBuyOrder().withPrice(145.1).withQuantity(25).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that, when a BUY order comes in, but no quantities are available, no trades are attempted.
     */
    @Test
    public void noTradeOccursForMismatchingSymbols() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.1).withQuantity(25).build())
                        .add(createDefaultBuyOrder().withPrice(145.1).withQuantity(25).withSymbol("OMC").build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that, when a BUY order comes in with a highly-precise price higher than the previous SELL, a trade occurs.
     * <p/>
     * One of the limitations of Java for testing a program like this is that the maximum precision values are obscured between the languages.
     * It may be that there's a way to work this out, but I'm not aware of immediately. I realize this is a very important test. In fact, the
     * exchange-sim currently rounds the sell price down, which seems like a flaw as it would be under-selling the orders. If the trade price
     * is going to be rounded, the same rounding should be applied to the prices before attempting to compare them.
     */
    @Test
    public void largeDecimalProcessesCorrectly() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(createDefaultSellOrder().withPrice(10.0000000001).withQuantity(25).build());
                orderListBuilder.add(createDefaultBuyOrder().withPrice(10.00000000011).withQuantity(25).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 10.00, 25)};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that a SELL is traded when a BUY order comes in at a higher price.
     */
    @Test
    public void higherPricedBuyComesAfterSell() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(41.01).withQuantity(100).build())
                        .add(createDefaultBuyOrder().withPrice(41.02).withQuantity(100).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 41.01, 100)};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that no trade occurs when a lower-priced BUY comes after a SELL.
     */
    @Test
    public void lowerPricedBuyComesAfterSell() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(41.01).withQuantity(100).build())
                        .add(createDefaultBuyOrder().withPrice(41.0).withQuantity(100).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that a BUY order is traded when a subsequent SELL order comes in at a lower price.
     */
    @Test
    public void lowerPricedSellComesAfterBuy() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultBuyOrder().withPrice(41.02).withQuantity(200).build())
                        .add(createDefaultSellOrder().withPrice(41.01).withQuantity(100).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 41.02, 100)};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that no trade occurs when a higher-priced SELL comes after a BUY.
     */
    @Test
    public void higherPricedSellComesAfterBuy() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultBuyOrder().withPrice(41.01).withQuantity(200).build())
                        .add(createDefaultSellOrder().withPrice(41.05).withQuantity(100).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that, when a BUY order comes in that exceeds the owned capacity for a symbol, only the owned capacity is traded.
     */
    @Test
    public void buyQuantityExceedsCapacity() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.09).withQuantity(50).build())
                        .add(createDefaultBuyOrder().withPrice(145.1).withQuantity(200).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 145.09, 50)};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that, when a BUY order comes in with a lower quantity than the current capacity for trading, the trade occurs with the BUY quantity.
     */
    @Test
    public void buyQuantityLowerThanCapacity() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.09).withQuantity(50).build())
                        .add(createDefaultBuyOrder().withPrice(145.1).withQuantity(25).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 145.09, 25)};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that, given a SELL order and a BUY order with a lower fractional value but a higher whole number value, no trade occurs.
     * <p/>
     * It may be that I'm naive here, but it seems to me like this is not the desired behavior.
     */
    @Test
    public void wholeNumberPriceDifferenceIgnored() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(2004.09).withQuantity(200).build())
                        .add(createDefaultBuyOrder().withPrice(9000.04).withQuantity(200).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        });
        performTestSteps();
    }
    
    /**
     * Verifies that, when a SELL comes in with a price equal to the price of a resting order, a trade occurs.
     */
    @Test
    public void restingOrder() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.09).withQuantity(100).build())
                        .add(createDefaultBuyOrder().withPrice(145.08).withQuantity(200).build())
                        .add(createDefaultBuyOrder().withPrice(145.09).withQuantity(200).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 145.09, 100)};
            }
        });

        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(createDefaultSellOrder().withPrice(145.09).withQuantity(100).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 145.09, 100)};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that, when a SELL order comes in while there are multiple resting BUY orders with higher but disparate prices, the highest-priced BUY's trade first.
     */
    @Test
    public void highestBuySellsFirst() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultBuyOrder().withPrice(145.1).withQuantity(200).build())
                        .add(createDefaultBuyOrder().withPrice(145.08).withQuantity(200).build())
                        .add(createDefaultBuyOrder().withPrice(145.09).withQuantity(200).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        });

        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(createDefaultSellOrder().withPrice(145.05).withQuantity(600).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{
                        new TradeMatcher(DEFAULT_SYMBOL, 145.1, 200),
                        new TradeMatcher(DEFAULT_SYMBOL, 145.09, 200),
                        new TradeMatcher(DEFAULT_SYMBOL, 145.08, 200)};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that, when two SELL orders with the same symbol and price are both resting, a BUY that has the same price causes two separate trades, ostensibly trading the oldest one
     * first (there's no unique identifier to adequately verify this).
     */
    @Test
    public void oldestRestingOrderSoldFirste() {
        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder
                        .add(createDefaultSellOrder().withPrice(145.09).withQuantity(200).build())
                        .add(createDefaultSellOrder().withPrice(145.09).withQuantity(200).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{};
            }
        });

        registerTestStep(new OrderTestStep() {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(createDefaultBuyOrder().withPrice(145.09).withQuantity(400).build());
            }

            @Override
            protected Matcher<Trade>[] getTradeMatchers() {
                return new Matcher[]{new TradeMatcher(DEFAULT_SYMBOL, 145.09, 200), new TradeMatcher(DEFAULT_SYMBOL, 145.09, 200)};
            }
        });
        performTestSteps();
    }

    private abstract class OrderTestStep extends harness.ExchangeSimulatorTestStep {

        /**
         * The default order symbol used in {@link #createDefaultSellOrder()} and {@link #createDefaultBuyOrder()}.
         */
        protected final String DEFAULT_SYMBOL = "IBM";

        /**
         * Constructs a new {@link OrderTradeTest}.
         */
        public OrderTestStep() {
            super(OrderTradeTest.this);
        }

        /**
         * @inheritDoc <p />
         * Overridden as abstract to force tests to implement. For these tests, the outputted trades are essential to the understanding of what the
         * test case is attempting to accomplish, so it's prudent that each test provide an explicit implementation.
         */
        @Override
        protected abstract Matcher<Trade>[] getTradeMatchers();

        /**
         * @return Non-null order for a SELL action with the {@value OrderTestStep##DEFAULT_SYMBOL}.
         */
        protected final Order.Builder createDefaultSellOrder() {
            return Order.Builder.create().withSymbol(DEFAULT_SYMBOL).withAction("SELL");
        }

        /**
         * @return Non-null order for a BUY action with the {@value OrderTestStep##DEFAULT_SYMBOL}.
         */
        protected final Order.Builder createDefaultBuyOrder() {
            return Order.Builder.create().withSymbol(DEFAULT_SYMBOL).withAction("BUY");
        }
    }
}

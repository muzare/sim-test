import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.junit.Test;
import simulator.input.Order;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Contains tests for verifying the behavior of the exchange simulator when given invalid inputs.
 */
public final class ValidationTest extends ExchangeSimulatorTestHarness {

    /**
     * Verifies that an order containing a zero price causes the exchange simulation to abort.
     */
    @Test
    public void zeroPrice() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withPrice(0.0);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        }.performTest();
    }

    /**
     * Verifies that an order containing a negative price causes the exchange simulation to abort.
     */
    @Test
    public void negativePrice() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withPrice(-148.0);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        }.performTest();
    }

    /**
     * Verifies that an order containing a zero quantity causes the exchange simulation to abort.
     */
    @Test
    public void zeroQuantity() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withQuantity(0);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        }.performTest();
    }

    /**
     * Verifies that an order containing a negative quantity causes the exchange simulation to abort.
     */
    @Test
    public void negativeQuantity() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withQuantity(-100);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        }.performTest();
    }

    /**
     * Verifies that an order containing an empty symbol causes the exchange simulation to abort.
     */
    @Test
    public void emptySymbol() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withSymbol("");
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        }.performTest();
    }

    /**
     * Verifies that an order containing an symbol with more than six characters causes the exchange simulation to abort.
     */
    @Test
    public void symbolWithOverSixCharacters() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withSymbol("IBMIBMI");
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        }.performTest();
    }

    /**
     * Verifies that an order containing an empty action causes an "invalid side" warning.
     */
    @Test
    public void emptyAction() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withAction("");
            }

            @Override
            protected Matcher<String>[] getWarningMatchers() {
                return new Matcher[]{equalTo("invalid side: ")};
            }
        }.performTest();
    }

    /**
     * Verifies that an order containing an unrecognized action causes an "invalid side" warning.
     */
    @Test
    public void unrecognizedAction() {
        new ValidationTestCase() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withAction("EAT");
            }

            @Override
            protected Matcher<String>[] getWarningMatchers() {
                return new Matcher[]{equalTo("invalid side: EAT")};
            }
        }.performTest();
    }

    protected abstract class ValidationTestCase extends ExchangeSimulatorTestCase {

        public ValidationTestCase() {
            super(ValidationTest.this);
        }

        protected abstract void invalidateOrder(final Order.Builder orderBuilder);

        @Override
        protected final void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
            final Order.Builder validOrder = Order.Builder.create().withSymbol("IBM").withAction("SELL").withPrice(145.09).withQuantity(100);
            invalidateOrder(validOrder);
            orderListBuilder.add(validOrder.build());
        }
    }
}

package validation;

import com.google.common.collect.ImmutableList;
import harness.ExchangeSimulatorTestHarness;
import harness.ExchangeSimulatorTestStep;
import org.hamcrest.Matcher;
import org.junit.Test;
import simulator.input.Order;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Contains tests for verifying the behavior of the exchange simulator when given invalid inputs.
 */
public final class OrderValidationTest extends ExchangeSimulatorTestHarness {

    /**
     * Verifies that an order containing a zero price causes the exchange simulation to abort.
     */
    @Test
    public void zeroPrice() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withPrice(0.0);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that an order containing a negative price causes the exchange simulation to abort.
     */
    @Test
    public void negativePrice() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withPrice(-148.0);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that an order containing a zero quantity causes the exchange simulation to abort.
     */
    @Test
    public void zeroQuantity() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withQuantity(0);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that an order containing a negative quantity causes the exchange simulation to abort.
     */
    @Test
    public void negativeQuantity() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withQuantity(-100);
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that an order containing an empty symbol causes the exchange simulation to abort.
     */
    @Test
    public void emptySymbol() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withSymbol("");
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that an order containing an symbol with more than six characters causes the exchange simulation to abort.
     */
    @Test
    public void symbolWithOverSixCharacters() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withSymbol("IBMIBMI");
            }

            @Override
            protected boolean shouldProcessAbort() {
                return true;
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that an order containing an empty action causes an "invalid side" warning.
     */
    @Test
    public void emptyAction() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withAction("");
            }

            @Override
            protected Matcher<String>[] getWarningMatchers() {
                return new Matcher[]{equalTo("invalid side: ")};
            }
        });
        performTestSteps();
    }

    /**
     * Verifies that an order containing an unrecognized action causes an "invalid side" warning.
     */
    @Test
    public void unrecognizedAction() {
        registerTestStep(new ValidationTestStep() {

            protected void invalidateOrder(final Order.Builder orderBuilder) {
                orderBuilder.withAction("EAT");
            }

            @Override
            protected Matcher<String>[] getWarningMatchers() {
                return new Matcher[]{equalTo("invalid side: EAT")};
            }
        });
        performTestSteps();
    }

    private abstract class ValidationTestStep extends ExchangeSimulatorTestStep {

        public ValidationTestStep() {
            super(OrderValidationTest.this);
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

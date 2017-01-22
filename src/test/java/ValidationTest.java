import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.joda.time.Seconds;
import org.junit.Rule;
import org.junit.Test;
import simulator.ExchangeSimulatorRuntime;
import simulator.input.Order;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by adam on 1/21/17.
 */
public class ValidationTest {

    @Rule
    public final ExchangeSimulatorRuntime simulatorRuntime = new ExchangeSimulatorRuntime(Seconds.TWO);

    /**
     * Verifies that an order containing a negative price causes the exchange simulation to abort.
     */
    @Test
    public void negativePrice() {
        new TestPlan(this) {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("SELL").withPrice(-145.09).withQuantity(100).build());
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
        new TestPlan(this) {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("SELL").withPrice(145.09).withQuantity(-100).build());
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
        new TestPlan(this) {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(Order.Builder.create().withSymbol("").withAction("SELL").withPrice(145.09).withQuantity(100).build());
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
        new TestPlan(this) {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(Order.Builder.create().withSymbol("IBMIBMI").withAction("SELL").withPrice(145.09).withQuantity(100).build());
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
        new TestPlan(this) {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("").withPrice(145.09).withQuantity(100).build());
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
        new TestPlan(this) {

            protected void populateOrders(final ImmutableList.Builder<Order> orderListBuilder) {
                orderListBuilder.add(Order.Builder.create().withSymbol("IBM").withAction("EAT").withPrice(145.09).withQuantity(100).build());
            }

            @Override
            protected Matcher<String>[] getWarningMatchers() {
                return new Matcher[]{equalTo("invalid side: EAT")};
            }
        }.performTest();
    }

}

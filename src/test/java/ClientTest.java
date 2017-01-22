import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import simulator.ExchangeSimulatorClient;
import simulator.input.Order;

import java.io.IOException;
import java.util.List;

/**
 * Created by adam on 1/21/17.
 */
public class ClientTest {

    private ExchangeSimulator simulator;

    @Before
    public void initializeSimulator() {

    }

    @Test
    public void err() {

        final Order order1 = Order.Builder
                .create().withSymbol("IBM").withAction("SELL").withPrice(145.09).withQuantity(100).build();

        final Order order2 = Order.Builder
                .create().withSymbol("IBM").withAction("BUY").withPrice(145.08).withQuantity(200).build();

        final Order order3 = Order.Builder
                .create().withSymbol("IBM").withAction("BUY").withPrice(145.09).withQuantity(200).build();

        final Order order4 = Order.Builder
                .create().withSymbol("IBM").withAction("SELL").withPrice(145.09).withQuantity(100).build();

        final List<Order> orders = ImmutableList.of(order1, order2, order3, order4);

        try {
            final ExchangeSimulatorClient client = new ExchangeSimulatorClient();
            client.processOrder(ImmutableList.of(order1, order2, order3, order4));
            client.endSimulation();
        } catch (final IOException e) {
            System.out.println(e.getStackTrace());
        }
        System.out.println("yipp");
    }
}

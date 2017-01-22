import com.google.common.collect.ImmutableList;
import org.junit.Test;
import simulator.ExchangeSimulatorClient;
import simulator.input.Order;

import java.io.IOException;

/**
 * Created by adam on 1/21/17.
 */
public class ClientTest {


    @Test
    public void err() {
        final Order order1 = Order.Builder
                .create().withSymbol("ASD").withAction("SELL").withPrice(145.09).withQuantity(100).build();

        final Order order2 = Order.Builder
                .create().withSymbol("ASD").withAction("BUY").withPrice(145.08).withQuantity(200).build();

        final Order order3 = Order.Builder
                .create().withSymbol("ASD").withAction("BUY").withPrice(145.09).withQuantity(200).build();

        final Order order4 = Order.Builder
                .create().withSymbol("ASD").withAction("SELL").withPrice(145.09).withQuantity(100).build();

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

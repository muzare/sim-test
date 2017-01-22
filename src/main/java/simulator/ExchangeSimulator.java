package simulator;

import com.google.common.collect.ImmutableList;
import org.joda.time.Seconds;
import simulator.exception.HarnessInitializationError;
import simulator.exception.OrderProcessInputError;
import simulator.input.Order;
import simulator.output.Trade;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by adam on 1/21/17.
 */
public final class ExchangeSimulator {

    private static final String SIMULATOR_EXE = "exchange-sim";
    private final Process simulator;
    private final BufferedWriter processInputWriter;
    private final Seconds processingGracePeriod;

    public SimulationResults processOrders(final List<Order> orders) {
        try {
            processInputWriter.write(translateCommands(orders));
            processInputWriter.flush();
        } catch (final IOException exception) {
            throw new OrderProcessInputError(exception);
        }

        final RunnableStreamReader standardErr = new RunnableStreamReader(simulator.getErrorStream());
        final Thread receiveStandardErr = new Thread(standardErr);

        final RunnableStreamReader standardOut = new RunnableStreamReader(simulator.getInputStream());
        final Thread receiveStandardOut = new Thread(standardOut);

        receiveStandardErr.start();
        receiveStandardOut.start();

        final long millisToWait = processingGracePeriod.toStandardDuration().getMillis();
        try {
            receiveStandardErr.join(millisToWait);
            receiveStandardOut.join(millisToWait);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        if (!simulator.isAlive()) {
            return new SimulationResults(parseTrades(standardOut.getReadLines()), standardErr.getReadLines(), true);
        }
        return new SimulationResults(parseTrades(standardOut.getReadLines()), standardErr.getReadLines());
    }

    static ExchangeSimulator createSimulation(final Seconds processingGracePeriod) {

        final Process simulator;
        try {
            simulator = Runtime.getRuntime().exec(SIMULATOR_EXE);
        } catch (final IOException exception) {
            throw new HarnessInitializationError(exception);
        }

        return new ExchangeSimulator(simulator, new BufferedWriter(new OutputStreamWriter(simulator.getOutputStream())), processingGracePeriod);
    }

    void endSimulation() {
        simulator.destroy();
        try {
            processInputWriter.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static String translateCommands(final List<Order> orders) {
        final StringBuilder commandBuilder = new StringBuilder();
        for (final Order order : orders) {
            commandBuilder.append(order.getSymbol()).append('|').append(order.getAction()).append('|').append(order.getPrice()).append('|').append(order.getQuantity()).append('\n');
        }
        return commandBuilder.toString();
    }

    private static ImmutableList<Trade> parseTrades(final List<String> trades) {
        final ImmutableList.Builder translatedTrades = ImmutableList.builder();
        for (final String trade : trades) {
            final List<String> tokens = Arrays.asList(trade.split("\\|"));
            final Iterator<String> iterator = tokens.iterator();

            final String symbol = iterator.hasNext() ? iterator.next() : null;
            final Double price = iterator.hasNext() ? Double.valueOf(iterator.next()) : null;
            final Long quantity = iterator.hasNext() ? Long.valueOf(iterator.next()) : null;

            translatedTrades.add(new Trade(symbol, price, quantity));
        }
        return translatedTrades.build();
    }

    private ExchangeSimulator(final Process simulator, final BufferedWriter processInputWriter, final Seconds processingGracePeriod) {
        this.simulator = simulator;
        this.processInputWriter = processInputWriter;
        this.processingGracePeriod = processingGracePeriod;
    }
}

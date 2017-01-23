package simulator;

import com.google.common.collect.ImmutableList;
import org.joda.time.Seconds;
import simulator.exception.HarnessInitializationError;
import simulator.exception.OrderProcessInputError;
import simulator.exception.SimulationTerminationError;
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

    private static final long PROCESSING_PERIOD = 1000;
    private static final String SIMULATOR_EXE = "exchange-sim";

    private final Process simulator;
    private final BufferedWriter processInputWriter;
    private final RunnableStreamReader standardOut;
    private final RunnableStreamReader standardErr;

    public SimulationResults processOrders(final List<Order> orders) {
        try {
//            final String commands = translateOrdersToCommandLineInput(orders);
//            System.out.println(commands);
            processInputWriter.write(translateOrdersToCommandLineInput(orders));
            processInputWriter.flush();
        } catch (final IOException exception) {
            throw new OrderProcessInputError(exception);
        }

        // Allow time for the background stdout/stderr threads to read process output.
        try {
            Thread.sleep(PROCESSING_PERIOD);
        } catch (final InterruptedException exception) {
            System.err.println("Thread interrupted during background processing period.");
            exception.printStackTrace();
        }

        final SimulationResults results = new SimulationResults(parseTrades(standardOut.getReadLines()), standardErr.getReadLines(), !simulator.isAlive());
        standardOut.flush();
        standardErr.flush();
        return results;
    }

    static ExchangeSimulator createSimulation(final Seconds processingGracePeriod) {

        final Process simulatorProcess;
        try {
            simulatorProcess = Runtime.getRuntime().exec(SIMULATOR_EXE);
        } catch (final IOException exception) {
            throw new HarnessInitializationError(exception);
        }

        final ExchangeSimulator simulator = new ExchangeSimulator(simulatorProcess, new BufferedWriter(new OutputStreamWriter(simulatorProcess.getOutputStream())),
                new RunnableStreamReader(simulatorProcess.getInputStream()), new RunnableStreamReader(simulatorProcess.getErrorStream()));

        // Thread cleanup occurs in the #endSimulation; after simulator.destroy() is called, the input streams are emptied, and the threads return.
        new Thread(simulator.standardOut).start();
        new Thread(simulator.standardErr).start();

        return simulator;
    }

    void endSimulation() {
        simulator.destroy();
        try {
            processInputWriter.close();
        } catch (final IOException exception) {
            throw new SimulationTerminationError(exception);
        }
    }

    /**
     * Parses a List of {@link Order} objects into command-line input.
     * @param orders List of Orders to parse (cannot be null).
     * @return Non-null, possibly empty String from the parsed orders.
     */
    private static String translateOrdersToCommandLineInput(final List<Order> orders) {
        assert orders != null : "orders: null";
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

    private ExchangeSimulator(final Process simulator, final BufferedWriter processInputWriter, final RunnableStreamReader standardOut, final RunnableStreamReader standardErr) {
        this.simulator = simulator;
        this.processInputWriter = processInputWriter;
        this.standardOut = standardOut;
        this.standardErr = standardErr;
    }
}

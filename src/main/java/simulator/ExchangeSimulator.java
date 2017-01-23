package simulator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import simulator.exception.SimulationInitializationError;
import simulator.exception.SimulationTerminationError;
import simulator.exception.SimulatorInputError;
import simulator.input.Order;
import simulator.output.Trade;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper for interacting with the command-line interface of the exchange-sim program.
 */
public final class ExchangeSimulator {

    // Amount of time to allow for the simulator to process the input and produce output.
    private static final long PROCESSING_PERIOD = 1000;
    private static final String SIMULATOR_EXE = "exchange-sim";

    private final Process simulator;
    private final BufferedWriter processInputWriter;
    private final RunnableStreamReader standardOut;
    private final RunnableStreamReader standardErr;

    /**
     * Sends a List of {@link Order} objects through the simulator, returning the results of the simulation.
     *
     * @param orders List of Orders to process.
     * @return Non-null {@link SimulationResults} containing the results of the simulation.
     * @throw IllegalArgumentException if parameter conditions are not met.
     */
    public SimulationResults processOrders(final List<Order> orders) {
        Preconditions.checkArgument(orders != null, "Cannot pass a null List of Orders to processOrders");

        try {
            processInputWriter.write(translateOrdersToCommandLineInput(orders));
            processInputWriter.flush();
        } catch (final IOException exception) {
            throw new SimulatorInputError(exception);
        }

        // Allow time for the background stdout/stderr threads to read process output.
        try {
            Thread.sleep(PROCESSING_PERIOD);
        } catch (final InterruptedException exception) {
            System.err.println("Thread interrupted during background processing period.");
            exception.printStackTrace();
        }

        final SimulationResults results = new SimulationResults(parseTrades(standardOut.getReadLines()), standardErr.getReadLines(), !simulator.isAlive());

        // Flushing the stream readers ensures subsequent calls to processOrders only returns newly generated output.
        standardOut.flush();
        standardErr.flush();

        return results;
    }

    /**
     * Creates a new {@link ExchangeSimulator}.
     *
     * @return Non-null {@link ExchangeSimulator}.
     * @throws SimulationInitializationError if an error occurs trying to launch the {@link #SIMULATOR_EXE}.
     */
    static ExchangeSimulator createSimulation() {

        final Process simulatorProcess;
        try {
            simulatorProcess = Runtime.getRuntime().exec(SIMULATOR_EXE);
        } catch (final IOException exception) {
            throw new SimulationInitializationError(exception);
        }

        final ExchangeSimulator simulator = new ExchangeSimulator(simulatorProcess, new BufferedWriter(new OutputStreamWriter(simulatorProcess.getOutputStream())),
                new RunnableStreamReader(simulatorProcess.getInputStream()), new RunnableStreamReader(simulatorProcess.getErrorStream()));

        // Thread cleanup occurs in the #endSimulation; after simulator.destroy() is called, the input streams are emptied, and the run() method in the readers return.
        new Thread(simulator.standardOut).start();
        new Thread(simulator.standardErr).start();

        return simulator;
    }

    /**
     * Ends the current simulation, cleaning up related processes and threads.
     *
     * @throws SimulationTerminationError if an error occurs trying to close the {@link #processInputWriter}.
     */
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
     *
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

    /**
     * Parses trade output from the simulator into an object representation.
     *
     * @param trades List of Strings containing the trades output by the simulation (cannot be null).
     * @return Non-null, possibly empty ImmutableList of {@link Trade}s.
     */
    private static ImmutableList<Trade> parseTrades(final List<String> trades) {
        assert trades != null : "trades: null";
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

    /**
     * Private constructor to prevent direct instantiation (use {@link #createSimulation()}.
     *
     * @param simulator          The Process running the simulator (cannot be null).
     * @param processInputWriter A BufferedWriter for writing to the process' stdin (cannot be null).
     * @param standardOut        A {@link RunnableStreamReader} for reading from the process' stdout (cannot be null).
     * @param standardErr        A {@link RunnableStreamReader} for reading from the process' stderr (cannot be null).
     */
    private ExchangeSimulator(final Process simulator, final BufferedWriter processInputWriter, final RunnableStreamReader standardOut, final RunnableStreamReader standardErr) {
        assert simulator != null : "simulator: null";
        assert processInputWriter != null : "processInputWriter: null";
        assert standardOut != null : "standardOut: null";
        assert standardErr != null : "standardErr: null";

        this.simulator = simulator;
        this.processInputWriter = processInputWriter;
        this.standardOut = standardOut;
        this.standardErr = standardErr;
    }
}

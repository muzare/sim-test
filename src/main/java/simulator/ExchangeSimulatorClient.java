package simulator;

import simulator.input.Order;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by adam on 1/21/17.
 */
public final class ExchangeSimulatorClient {

    private static final String SIMULATOR_EXE = "exchange-sim";
    private final Process simulator;
    private final BufferedWriter inputWriter;

    public SimulationResults processOrder(final List<Order> orders) throws IOException {

        final String commands = translateCommands(orders);
        System.out.println(commands);

//        inputWriter.write(translateCommands(orders));
        final Thread receiveStandardErr = new Thread(new RunnableStreamReader(simulator.getErrorStream()));
        final Thread receiveStandardOut = new Thread(new RunnableStreamReader(simulator.getInputStream()));

        receiveStandardErr.start();
        receiveStandardOut.start();
        inputWriter.write(commands);
        try {
            receiveStandardErr.join(10000);
            receiveStandardOut.join(10000);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
//        simulator.destroy();
//        try {
//            final int exitCode = simulator.waitFor();
//        } catch (final InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Here is the standard output of the command:\n");
//        while ((output = stdOut.readLine()) != null) {
//            System.out.println(output);
//            System.out.println("and here's a boob..");
//        }

//        simulator.destroy();
//        return new SimulationResults(ImmutableList<Trade>of(), ImmutableList.of());
        return null;
    }

    public void endSimulation() {
        simulator.destroy();
    }


    private static String translateCommands(final List<Order> orders) {
        final StringBuilder commandBuilder = new StringBuilder();
        for (final Order order : orders) {
            commandBuilder.append(order.getSymbol()).append('|').append(order.getAction()).append('|').append(order.getPrice()).append('|').append(order.getQuantity()).append('\n');
        }
        return commandBuilder.toString();
    }

    public ExchangeSimulatorClient() throws IOException {
        simulator = Runtime.getRuntime().exec(SIMULATOR_EXE);
        inputWriter = new BufferedWriter(new OutputStreamWriter(simulator.getOutputStream()));
    }
}

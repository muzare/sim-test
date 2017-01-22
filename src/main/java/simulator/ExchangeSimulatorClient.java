package simulator;

import com.google.common.collect.ImmutableList;
import transaction.ExchangeTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by adam on 1/21/17.
 */
public final class ExchangeSimulatorClient {

    public TransactionResults sendTransaction(final ExchangeTransaction transaction) throws IOException {
        final Process simulator = Runtime.getRuntime().exec(translateCommands(ImmutableList.of(transaction)));

        String output = null;

        final BufferedReader stdOut = new BufferedReader(new InputStreamReader(simulator.getInputStream()));
        while ((output = stdOut.readLine()) != null) {
            System.out.println(output);
        }

//        return new TransactionResults(ImmutableList<Trade>of(), ImmutableList.of());
        return null;
    }


    private static String translateCommands(final ImmutableList<ExchangeTransaction> transactions) {
        final StringBuilder commandBuilder = new StringBuilder("exchange-sim");
        for (final ExchangeTransaction transaction : transactions) {
            commandBuilder.append(transaction.getTransactionType().toString()).append(transaction.getPrice()).append('\n');
        }
        return commandBuilder.toString();
    }

//    private static
}

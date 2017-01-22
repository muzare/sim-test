package simulator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by adam on 1/21/17.
 */
final class RunnableStreamReader implements Runnable {

    private final InputStream inputStream;
    private final List<String> readLines = Lists.newArrayList();

    RunnableStreamReader(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void run() {

        String output = null;
        try (final BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((output = streamReader.readLine()) != null) {
                System.out.println(output);
                readLines.add(output);
                System.out.println(readLines.size());
            }
        } catch (final IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    ImmutableList<String> getReadLines() {
        return ImmutableList.copyOf(readLines);
    }
}

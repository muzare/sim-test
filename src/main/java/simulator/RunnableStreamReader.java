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
public final class RunnableStreamReader implements Runnable {

    private final InputStream inputStream;
    private final List<String> readLines = Lists.newArrayList();

    public RunnableStreamReader(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void run() {
        final BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream));
        String output = null;

        try {
            while ((output = streamReader.readLine()) != null) {
                System.out.println(output);
                readLines.add(output);
            }
        } catch (final IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    public ImmutableList<String> getReadLines() {
        return ImmutableList.copyOf(readLines);
    }
}

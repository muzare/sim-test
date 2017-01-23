package simulator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Runnable for continuously reading from an {@link InputStream}. Read lines can be accessed via {@link #getReadLines()}. In order to get only the
 * latest output, it is necessary to {@link #flush()} the stream reader after retrieving read lines.
 */
final class RunnableStreamReader implements Runnable {

    private final InputStream inputStream;
    private final List<String> readLines = Lists.newArrayList();

    /**
     * Constructs a new {@link RunnableStreamReader}.
     *
     * @param inputStream @link InputStream to read from (cannot be null).
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    RunnableStreamReader(final InputStream inputStream) {
        Preconditions.checkArgument(inputStream != null, "RunnableStreamReader requires a non-null inputStream.");
        this.inputStream = inputStream;
    }

    /**
     * @inheritDoc <p />
     * Reads from the stream repeatedly until the underlying stream closes.
     */
    public void run() {
        String output = null;
        try (final BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((output = streamReader.readLine()) != null) {
                readLines.add(output);
            }
        } catch (final IOException exception) {
            System.err.println("Exception occurred while reading input stream.");
            System.err.println(exception.getStackTrace());
        }
        return;
    }

    /**
     * @return Non-null, possibly empty ImmutableList of lines read from the stream since construction of this instance or the last time {@link #flush()} was invoked.
     */
    ImmutableList<String> getReadLines() {
        return ImmutableList.copyOf(readLines);
    }

    /**
     * Flushes all previously read lines from this instance.
     */
    void flush() {
        readLines.clear();
    }
}

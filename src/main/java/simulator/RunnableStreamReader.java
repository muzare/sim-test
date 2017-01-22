package simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by adam on 1/21/17.
 */
public class RunnableStreamReader implements Runnable {

    private final InputStream inputStream;

    public RunnableStreamReader(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void run() {
        final BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream));
        String output = null;

        try {
//            for (int i = 0; i < 3; ++i) {
//                if ((output = streamReader.readLine()) == null) {
//                    continue;
//                }
//                System.out.println("got one... " + output);
            while ((output = streamReader.readLine()) != null) {
                System.out.println("and here's a boob..");
            }
        } catch (final IOException e) {
            System.out.println(e.getStackTrace());
        }
    }
}

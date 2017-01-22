import org.joda.time.Seconds;
import org.junit.Rule;
import simulator.ExchangeSimulatorRuntime;

/**
 * Created by adam on 1/22/17.
 */
public class ExchangeSimulatorTestHarness {

    /**
     * Rule that wraps the simulator client, ensuring the process is torn down and re-initialized after each test run.
     */
    @Rule
    public final ExchangeSimulatorRuntime simulatorRuntime = new ExchangeSimulatorRuntime(Seconds.TWO);
}

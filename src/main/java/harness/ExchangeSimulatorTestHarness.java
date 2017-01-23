package harness;

import com.google.common.collect.Lists;
import org.joda.time.Seconds;
import org.junit.After;
import org.junit.Rule;
import simulator.ExchangeSimulatorRuntime;

import java.util.List;

/**
 * Created by adam on 1/22/17.
 */
public abstract class ExchangeSimulatorTestHarness {
    private final List<ExchangeSimulatorTestStep> testSteps = Lists.newArrayList();

    /**
     * Rule that wraps the simulator client, ensuring the process is torn down and re-initialized after each test run.
     */
    @Rule
    public final ExchangeSimulatorRuntime simulatorRuntime = new ExchangeSimulatorRuntime(Seconds.THREE);

    @After
    public final void clearTestSteps() {
        testSteps.clear();
    }

    protected final void registerTestStep(final ExchangeSimulatorTestStep testStep) {
        testSteps.add(testStep);
    }

    protected final void performTestSteps() {
        testSteps.forEach(step -> step.performStep());
    }
}

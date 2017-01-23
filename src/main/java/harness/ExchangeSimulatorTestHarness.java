package harness;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Rule;
import simulator.ExchangeSimulatorRuntime;

import java.util.List;

/**
 * Test harness for writing test cases against the {@link simulator.ExchangeSimulator}.
 */
public abstract class ExchangeSimulatorTestHarness {
    private final List<ExchangeSimulatorTestStep> testSteps = Lists.newArrayList();

    /**
     * Rule that wraps the simulator client, ensuring the process is torn down and re-initialized after each test run.
     */
    @Rule
    public final ExchangeSimulatorRuntime simulatorRuntime = new ExchangeSimulatorRuntime();

    /**
     * Resets all test steps registered by the running test case.
     */
    @After
    public final void clearTestSteps() {
        testSteps.clear();
    }

    /**
     * Registers an {@link ExchangeSimulatorTestStep} for execution. Test steps are processed in FIFO order.
     *
     * @param testStep The test step to perform (cannot be null).
     * @throws IllegalArgumentException if the parameter conditions are not met.
     */
    protected final void registerTestStep(final ExchangeSimulatorTestStep testStep) {
        Preconditions.checkArgument(testStep != null, "Cannot register a null test step.");
        testSteps.add(testStep);
    }

    /**
     * Performs all {@link #registerTestStep(ExchangeSimulatorTestStep) registered test steps} in FIFO order.
     */
    protected final void performTestSteps() {
        testSteps.forEach(step -> step.performStep());
    }
}

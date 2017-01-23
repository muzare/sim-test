# exchange-sim-test
This is a framework with tests for the exchange-sim program.

# General information
## Usage
To build and run this, Java 8 and maven are required. There are some external dependencies I'm used to using (guava, hamcrest) which are being pulled in from the pom.xml. Furthermore, it's expected that the exchange-sim executable is on the $PATH. Finally, there is a grace period to allow the simulator time to compute/produce output; this is coded in the ExchangeSimulator. In extreme circumstances this period may not be long enough, which could cause failures.

## My approach

First, my reasoning for going with Java:
Between gtest/gmock and JUnit, I've done a bit more testing in Java. Furthemore, JUnit has some neat built-ins like the Theory which allow for combinatorial testing. In general, what I've done here represents a similar approach I would have taken in my last position: build some framework around the application to be tested to make test case writing relatively easy, and then write the test cases themselves. It may be that this is rather over-engineered, but it is what it is at this point. The implementation may have been smaller if I'd done the testing in python or c++.

Over-all, I probably spent 2-4 hours on this, subtracting some time for reading/research/problems I ran into around invoking a CLI via Java, since that is something I had not done before. I'm also learning Intellij IDEA (I used Eclipse before). I have a couple of criticisms I would address if more time was warranted, but I don't want to go too far beyond the recommended expenditure:
* The simulator/harness are built to allow for staggered input, e.g. 2 orders are sent, results asserted, 2 more are sent, results asserted. This is probably over-kill, knowing the program simply reads from std::cin in a loop. That said, there may be value in testing this scenario from a black-box perspective. In the end it may not be a worthwhile consideration, and so things could be simplified.
* I'd like to do more boundary testing around minimum/maximum values for price and quantity. This is something that is much easier in C++ due to the std::max/min functions; it becomes a bit obscured between the JVM and the underlying OS when using pure Java. It is probably possible but I don't know how to offhand.

# Design details

## Simulator
I wrote an ExchangeSimulator which is intended as a wrapper around the CLI. Along with that there are input/output classes for programmatically putting together a call to the simulator and reading the output. Since various exceptions thrown by the core Java libraries cannot really be handled effectively, I've wrapped some of them in somewhat more descriptive RuntimeExceptions. Where the exception seemed non-consequential (or had the potential to be so) I simply wrote the stack trace to stderr.

The essential setup/teardown functions in the ExchangeSimulator are made package scope. The only thing exposed to tests is the processOrders method; an ExternalResource JUnit @Rule wraps the initialization/teardown to guarantee resources are handled properly.

## TestHarness/TestStep
The idea here is to keep all of the standard testing steps in a centralized, logical place that can be reused. The TestHarness contains the appropriate JUnit Rules and a queue of TestSteps to perform. Each test class extends the TestHarness and populates the steps needed by the test. In practice, I subclasses TestStep to provide reusable functionality common through test cases in a test class. 

With the TradeMatcher I think the over-all redundancy/boilerplate is reduced a bit; I like the test cases to be self-explanatory but adequately describe all of the inputs and expectations.

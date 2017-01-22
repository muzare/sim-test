# exchange-sim-test
This is a testing .jar to test the exchange-sim program.

# General information
## Usage
To build and run this, Java 8 and maven are required. There are some external dependencies I'm used to using (guava, hamcrest) which are being pulled in from the pom.xml. Furthermore, it's expected that the exchange-sim executable is on the $PATH.

## My approach

First, my reasoning for going with Java:
Between gtest/gmock and JUnit, I've done a bit more testing in Java. What I've done here represents a similar approach I would have taken in my last position: build some framework around the application to be tested to make test case writing relatively easy, and then write the test cases themselves. In case this seems like over-kill, I also intend to work on a C++ testing framework which would probably end up as a smaller implementation.

Over-all, I probably spent 2-4 hours on this, subtracting some time for reading/research/problems I ran into around invoking a CLI via Java, since that is something I had not done before. I'm also learning Intellij IDEA (I used Eclipse before). I have a couple of criticisms I would address if more time was warranted, but I don't want to go too far beyond the recommended expenditure:
* It would be useful to have more multi-input tests, e.g. one set of input is sent, results asserted, and then another round of input goes out. The "resting order" functionality is not covered as well as it could be.
* I'd like to do more boundary testing around minimum/maximum values for price and quantity. This is something that is much easier in C++ due to the std::max/min functions; it becomes a bit obscured between the JVM and the underlying OS when using pure Java.

# Design details

## Simulator
I wrote an ExchangeSimulator which is intended as a wrapper around the CLI. Along with that there are input/output classes for programmatically putting together a call to the simulator and reading the output. Since the various IOExceptions cannot really be handled effectively, I've wrapped them in somewhat more descriptive RuntimeExceptions.

I did some research for reading and writing to the process file descriptors, and there appear to be many "gotchas," particularly that some operating systems will block reading from stdout if stderr contains messages. That's the reasoning around the RunnableStreamReader class.

## TestHarness/TestStep
The idea here is to keep all of the standard testing steps in a centralized, logical place that canb e reused. The TestHarness supports multiple actions. The tests with single actions are complicated a bit by this, so it could be improved, but my focus goal was flexibility. With the TradeMatcher I think the over-all redundancy/boilerplate is reduced a bit; I like the test cases to be self-explanatory but adequately describe all of the inputs and expectations.

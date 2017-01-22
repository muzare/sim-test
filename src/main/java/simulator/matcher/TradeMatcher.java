package simulator.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import simulator.output.Trade;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by adam on 1/22/17.
 */
public class TradeMatcher extends TypeSafeDiagnosingMatcher<Trade> {

    private final Matcher<String> symbolMatcher;
    private final Matcher<Double> priceMatcher;
    private final Matcher<Long> quantityMatcher;

    public TradeMatcher(final String expectedSymbol, final double expectedPrice, final long expectedQuantity) {
        symbolMatcher = equalTo(expectedSymbol);
        priceMatcher = equalTo(expectedPrice);
        quantityMatcher = equalTo(Long.valueOf(expectedQuantity));
    }

    /**
     * @inheritDoc
     */
    protected boolean matchesSafely(final Trade actualTrade, final Description description) {
        boolean matches = true;

        if (!symbolMatcher.matches(actualTrade.getSymbol())) {
            matches &= false;
            symbolMatcher.describeTo(description);
        }
        if (!priceMatcher.matches(actualTrade.getPrice())) {
            matches &= false;
            priceMatcher.describeTo(description);
        }
        if (!quantityMatcher.matches(actualTrade.getQuantity())) {
            matches &= false;
            quantityMatcher.describeTo(description);
        }
        return matches;
    }

    /**
     * @inheritDoc
     */
    public void describeTo(final Description description) {
        description.appendDescriptionOf(symbolMatcher).appendDescriptionOf(quantityMatcher).appendDescriptionOf(priceMatcher);
    }
}

package simulator.output;

/**
 * Created by adam on 1/21/17.
 */
public class Trade {

    private final String symbol;
    private final Double price;
    private final Long quantity;

    public String getSymbol() {
        return symbol;
    }

    public Double getPrice() {
        return price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Trade(final String symbol, final Double price, final Long quantity) {

        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
    }
}

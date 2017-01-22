package simulator.input;

/**
 * Created by adam on 1/21/17.
 */
public class Order {

    private final String symbol;
    private final String action;
    private final double price;
    private final long quantity;

    public String getSymbol() {
        return symbol;
    }

    public String getAction() {
        return action;
    }

    public double getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public static class Builder {

        private String symbol = "";
        private String transaction = "";
        private double price = 0.0;
        private long quantity = 0;

        public static Builder create() {
            return new Builder();
        }

        public Builder withSymbol(final String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder withAction(final String transactionType) {
            this.transaction = transactionType;
            return this;
        }

        public Builder withPrice(final double price) {
            this.price = price;
            return this;
        }

        public Builder withQuantity(final long quantity) {
            this.quantity = quantity;
            return this;
        }

        public Order build() {
            return new Order(symbol, transaction, price, quantity);
        }

        private Builder() {
        }
    }

    private Order(final String symbol, final String action, final double price, final long quantity) {
        this.symbol = symbol;
        this.action = action;
        this.price = price;
        this.quantity = quantity;
    }
}

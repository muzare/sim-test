package transaction;

import com.google.common.base.Preconditions;

/**
 * Created by adam on 1/21/17.
 */
public final class BuyTransaction implements ExchangeTransaction {

    private final String symbol;
    private final TransactionType transactionType;
    private final double price;
    private final long quantity;

    public double getPrice() {
        return price;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public static class Builder {

        private String symbol = null;
        private TransactionType transactionType = null;
        private Double price = null;
        private Long quantity = null;

        public Builder withSymbol(final String symbol) {
            Preconditions.checkArgument(symbol.length() <= 6 && symbol.length() > 0, "symbol.length() must be 0 < x <= 6 (actual: " + symbol.length() + ")");
            this.symbol = symbol;
            return this;
        }

        public Builder withTransactionType(final TransactionType transactionType) {
            this.transactionType = transactionType;
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

        public BuyTransaction build() {
            Preconditions.checkArgument(symbol != null, "symbol cannot be null");
            Preconditions.checkArgument(transactionType != null, "transactionType cannot be null");
            Preconditions.checkArgument(price >= 0, "price cannot be negative");
            Preconditions.checkArgument(quantity >= 0, "quantity cannot be negative");

            return new BuyTransaction(symbol, transactionType, price, quantity);
        }

    }

    private BuyTransaction(final String symbol, final TransactionType transactionType, final double price, final long quantity) {
        this.symbol = symbol;
        this.transactionType = transactionType;
        this.price = price;
        this.quantity = quantity;
    }
}
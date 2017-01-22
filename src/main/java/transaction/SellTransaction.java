package transaction;

/**
 * Created by adam on 1/21/17.
 */
public final class SellTransaction implements ExchangeTransaction {
    public double getPrice() {
        return 0;
    }

    public final TransactionType getTransactionType() {
        return null;
    }
}

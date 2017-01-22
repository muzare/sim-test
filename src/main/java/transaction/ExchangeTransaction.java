package transaction;

/**
 * Created by adam on 1/21/17.
 */
public interface ExchangeTransaction {

    double getPrice();

    TransactionType getTransactionType();
}

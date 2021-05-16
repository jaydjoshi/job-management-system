package com.dd.jobmangementsystem.batch.processor;


import com.dd.jobmangementsystem.batch.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Logger log = LoggerFactory.getLogger(TransactionItemProcessor.class);

    @Override
    public Transaction process(final Transaction transaction) throws Exception {
        final int id = transaction.getPersonId();
        final String transactionDate = transaction.getTransactionDate();
        final double amount = transaction.getAmount() * Math.random(); // to mock real world FX rates

        final Transaction transaction1 = new Transaction(id, transactionDate, amount);

        log.info("Converting (" + transaction + ") into (" + transaction1 + ")");

        return transaction1;
    }

}
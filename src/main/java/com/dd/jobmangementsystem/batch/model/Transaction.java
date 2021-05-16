package com.dd.jobmangementsystem.batch.model;

import java.time.LocalDateTime;

public class Transaction {

    private int personId;
    private String transactionDate;
    private double amount;

    public Transaction() {
    }

    public Transaction(int personId, String transactionDate, double amount) {
        this.personId = personId;
        this.transactionDate = transactionDate;
        this.amount = amount;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "personId=" + personId +
                ", transactionDate=" + transactionDate +
                ", amount=" + amount +
                '}';
    }
}
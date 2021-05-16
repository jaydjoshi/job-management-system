package com.dd.jobmangementsystem.batch.constants;

public class QueryConstants {

    public static String PERSON_QUERY = "INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)";
    public static String SELECT_PERSON_QUERY = "SELECT id, first_name, last_name FROM people";

    public static String TRANSACTION_QUERY = "INSERT INTO transaction_detail ( transaction_date, amount) VALUES ( :transactionDate, :amount )";
}

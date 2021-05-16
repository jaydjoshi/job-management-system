DROP TABLE IF EXISTS people;
DROP TABLE IF EXISTS transaction_detail;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

CREATE TABLE transaction_detail  (
    person_id BIGINT IDENTITY NOT NULL,
    transaction_date VARCHAR(20),
    amount NUMBER
);
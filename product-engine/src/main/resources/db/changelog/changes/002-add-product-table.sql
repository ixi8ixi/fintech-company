DROP TABLE customer;

CREATE TABLE products (
  product_code VARCHAR(100) PRIMARY KEY,
  loan_term_min INTEGER NOT NULL,
  loan_term_max INTEGER NOT NULL,
  principal_amount_min NUMERIC(20, 5) NOT NULL,
  principal_amount_max NUMERIC(20, 5) NOT NULL,
  interest_min NUMERIC(20, 5) NOT NULL,
  interest_max NUMERIC(20, 5) NOT NULL,
  origination_amount_min NUMERIC(20, 5) NOT NULL,
  origination_amount_max NUMERIC(20, 5) NOT NULL
);

INSERT INTO products (product_code,
                      loan_term_min,
                      loan_term_max,
                      principal_amount_min,
                      principal_amount_max,
                      interest_min,
                      interest_max,
                      origination_amount_min,
                      origination_amount_max)
VALUES ('CL1.0.0', 3, 24, 50000, 500000, 8, 15, 2000, 10000);

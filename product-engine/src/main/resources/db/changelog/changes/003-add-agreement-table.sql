CREATE TABLE agreements (
    agreement_id BIGINT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    loan_term INTEGER NOT NULL,
    disbursement_amount NUMERIC(20, 5) NOT NULL,
    principal_amount NUMERIC(20, 5) NOT NULL,
    interest NUMERIC(20, 5) NOT NULL,
    product_code VARCHAR(100) NOT NULL
);
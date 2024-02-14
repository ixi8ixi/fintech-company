CREATE TABLE schedules(
  schedule_id BIGINT PRIMARY KEY,
  agreement_id BIGINT NOT NULL,
  schedule_version INTEGER NOT NULL
);

CREATE TABLE schedule_payments(
  payment_id BIGINT PRIMARY KEY,
  schedule_id BIGINT NOT NULL,
  status VARCHAR(100) NOT NULL,
  payment_date date NOT NULL,
  period_payment NUMERIC(20, 5) NOT NULL,
  interest_payment NUMERIC(20, 5) NOT NULL,
  principal_payment NUMERIC(20, 5) NOT NULL,
  period_number INTEGER
);
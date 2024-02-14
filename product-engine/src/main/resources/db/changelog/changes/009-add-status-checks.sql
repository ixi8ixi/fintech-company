ALTER TABLE agreements
ADD CONSTRAINT check_agreement_status CHECK (agreements.status IN ('PENDING', 'ACCEPTED', 'REJECTED'));

ALTER TABLE schedule_payments
ADD CONSTRAINT check_payment_status CHECK (schedule_payments.status IN ('PENDING', 'PAID', 'OUTDATED'))
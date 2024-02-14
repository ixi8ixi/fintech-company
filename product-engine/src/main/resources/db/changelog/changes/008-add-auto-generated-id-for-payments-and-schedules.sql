CREATE SEQUENCE schedule_id_seq START 1;

ALTER TABLE schedules
ALTER COLUMN schedule_id SET DEFAULT nextval('schedule_id_seq'::regclass),
ALTER COLUMN schedule_id SET NOT NULL;

CREATE SEQUENCE payment_id_seq START 1;

ALTER TABLE schedule_payments
ALTER COLUMN payment_id SET DEFAULT nextval('payment_id_seq'::regclass),
ALTER COLUMN payment_id SET NOT NULL;
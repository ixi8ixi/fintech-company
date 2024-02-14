CREATE SEQUENCE new_id_seq START 1;

ALTER TABLE agreements
ALTER COLUMN agreement_id SET DEFAULT nextval('new_id_seq'::regclass),
ALTER COLUMN agreement_id SET NOT NULL;
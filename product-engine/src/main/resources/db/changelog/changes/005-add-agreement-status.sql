DELETE FROM agreements;

ALTER TABLE agreements
ADD COLUMN status VARCHAR(100);

ALTER TABLE agreements
ALTER COLUMN status SET NOT NULL;
CREATE TABLE clients (
   id VARCHAR(30) DEFAULT nextval('client_id'::regclass)::varchar PRIMARY KEY,
   first_name VARCHAR(100) NOT NULL,
   last_name VARCHAR(100) NOT NULL,
   email VARCHAR(100) NOT NULL,
   salary NUMERIC(20, 5) NOT NULL,
   UNIQUE(email)
);

CREATE TABLE applications (
   id VARCHAR(30) DEFAULT nextval('application_id'::regclass)::varchar PRIMARY KEY,
   client_id VARCHAR(30) NOT NULL REFERENCES clients (id),
   requested_disbursement_amount NUMERIC(20, 5) NOT NULL,
   status VARCHAR(50) NOT NULL
      CHECK (status IN ('NEW', 'SCORING_ACCEPTED', 'SCORING_REJECTED', 'ACTIVE', 'CLOSED', 'CANCELED'))
);
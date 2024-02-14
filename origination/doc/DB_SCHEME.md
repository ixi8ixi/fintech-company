# Database Schema
The module utilizes 2 tables:
1. Clients table (clients)
2. Applications table (applications)

## Clients table
The table contains columns:
1. (ID VARCHAR) **id** - client id
2. (VARCHAR) **first_name** - client first name.
3. (VARCHAR) **last_name** - client last name
4. (VARCHAR) **email** - client email
5. (NUMERIC) **salary** - client salary

## Applications table
The table contains columns:
1. (ID VARCHAR) **id** - application id
2. (VARCHAR) **client_id** - the ID of the client submitting the application, references clients id.
3. (NUMERIC) **requested_disbursement_amount** - requested disbursement amount
4. (VARCHAR) **status** - current application status, can be on of the 'NEW', 'SCORING', 
'ACCEPTED', 'ACTIVE', 'CLOSED', 'DENIED'
# Database Schema
The module utilizes 4 tables:
1. Products table (products)
2. Agreements table (agreements)
3. Payment schedules table (schedules)
4. Loan payment table (schedule_payments)

## Products Table
The table contains columns:
1. (ID VARCHAR) **product_code** - product name and its version
2. (INTEGER) **loan_term_min**, **loan_term_max** - permissible boundaries of the loan duration in months.
3. (NUMERIC) **principal_amount_min**, **principal_amount_max** - permissible boundaries of the loan amount
4. (NUMERIC) **interest_min**, **interest_max** - permissible boundaries of the credit interest rate
5. (NUMERIC) **origination_amount_min**, **origination_amount_max** - permissible boundaries of the commission fee

The table serves as a repository for all products ever provided. Currently, it is used for verifying 
requests to create a new agreement.

## Agreements table
The table contains columns:
1. (ID BIGINT) **agreement_id** - agreement id
2. (BIGINT) **client_id** - customer ID taking a loan
3. (INT) **loan_term** - loan repayment duration in months
4. (NUMERIC) **disbursement_amount** - the money that the client will receive after the loan is approved
5. (NUMERIC) **principal_amount** - the amount that the client will owe to the bank without interest
6. (NUMERIC) **interest** - interest rate on the loan
7. (VARCHAR) **product_code** - the version of the product for which the loan will be paid
8. (VARCHAR) **status** - current agreement status, could be only 'PENDING', 'ACCEPTED' or 'REJECTED'
9. (DATE) **disbursement_date** - the day the client received the money, can be null if agreement status is
'PENDING' or 'REJECTED'
10. (DATE) **next_payment_date** - the next payment day, can be null if agreement status is
    'PENDING' or 'REJECTED', or if it is the last payment by this agreement

After the verification of the request to add a new loan, a record about it is added to this table 
with the status PENDING. After that, there are two possibilities: confirmation - the record 
changes its status to ACCEPTED, and the fields disbursement_date and next_payment_date are filled 
with values, or rejection - the record changes its status to REJECTED.

## Schedules table
The table contains columns:
1. (ID BIGINT) **schedule_id** - unique schedule code
2. (BIGINT) **agreement_id** - the agreement for which this schedule was created 
3. (INTEGER) **schedule_version** - the version of the schedule created for this loan

When the loan is approved, a new payment schedule is created and saved in this table. 
Additionally, payments are generated and stored in the 'schedule_payments' table.

## Payments table
The table contains columns:
1. (ID BIGINT) **payment_id** - unique payment id
2. (BIGINT) **schedule_id** - the schedule to which this payment belongs
3. (VARCHAR) **status** - current payment status, could be 'PENDING', 'PAID' or 'OUTDATED'
4. (DATE) **payment_date** - the date by which the specified payment must be made without penalties
5. (NUMERIC) **period_payment** - the fixed monthly payment for the loan
6. (NUMERIC) **interest_payment** - interest portion in this payment
7. (NUMERIC) **principal_payment** - principal portion in this payment
8. (INTEGER) **period_number** - the sequence number of the payment in this schedule

When a new payment schedule is created, a list of payments is generated and stored in this table. 
Initially, the payment has a status of PENDING; after payment, the status changes to PAID. If the 
current loan schedule is changed, the payment status is updated to OUTDATED.
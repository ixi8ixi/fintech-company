# GRPC contracts description
A brief explanation of the returned values in the contracts described in the [proto-dir](../src/main/proto)
## Agreement service v1 ([file](../src/main/proto/AgreementServiceV1.proto))

Both requests (AddAgreement and Disbursement) return EngineResponse. In both cases, 
if an error occurs during request processing, a negative error code is returned along 
with an explanatory message in the 'message' field (see details below). Now, let's 
delve into each of the requests in more detail:

### AddAgreement
If no error occurs during the processing of the request, it returns the number of 
the created loan and an empty explanatory message. Otherwise, it returns one of the 
three error codes: -1, -2, or -3.

+ -1 - Format check error: at least one of the fields disbursementAmount, 
originationAmount, or interest has an incorrect numerical format.
+ -2 - Validation error: the product with the specified identifier does not exist, 
or at least one of the values is out of bounds.
+ -3 - Processing error: error while creating agreement

### Disbursement
If no error occurs during the processing of the request, it returns the id of created 
schedule and an empty explanatory message. Otherwise, it returns one of two 
error codes: -1, or -3.

+ -1 - Format check error: date is in invalid format
+ -3 - Processing error: if it is impossible to create a new payment schedule 
for any reason 
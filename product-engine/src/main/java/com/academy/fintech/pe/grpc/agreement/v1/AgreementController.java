package com.academy.fintech.pe.grpc.agreement.v1;

import com.academy.fintech.pe.core.service.agreement.AgreementCreationService;
import com.academy.fintech.pe.core.service.agreement.DisbursementService;
import com.academy.fintech.pe.core.service.agreement.ValidationService;
import com.academy.fintech.pe.core.service.agreement.operation.result.OperationResult;
import com.academy.fintech.pe.grpc.agreement.v1.dto.AgreementDto;
import com.academy.fintech.pe.grpc.agreement.v1.dto.DisbursementDto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.time.format.DateTimeParseException;

/**
 * Controller for handling gRPC requests incoming to the PaymentEngine.
 * <p>
 * The processing is carried out in three stages:
 * <pre>
 * 1. (optional) Format check (checking the format of fields, such as dates or floating-point numbers)
 * 2. (optional) Request validation (checking the correctness of values, such as the existence of a product,
 *    validation of values within specified boundaries)
 * 3. Execution of the operation and returning the result with a message
 * </pre>
 * <p>
 * Validation occurs in the controller; corresponding services are employed for verification and
 * execution.
 */
@GRpcService
@RequiredArgsConstructor
public class AgreementController extends AgreementServiceGrpc.AgreementServiceImplBase {
    private static final long FORMAT_CHECK_ERROR_CODE = -1;
    private static final long VALIDATION_ERROR_CODE = -2;
    private static final EngineResponse AGREEMENT_FORMAT_CHECK_FAILED
            = makeFormatCheckErrorResponse("INVALID NUMBER FORMAT");
    private static final EngineResponse DISBURSEMENT_FORMAT_CHECK_FAILED
            = makeFormatCheckErrorResponse("INVALID DATE FORMAT");

    private final ValidationService validationService;
    private final AgreementCreationService agreementCreationService;
    private final DisbursementService disbursementService;

    /**
     * Processing stages:
     * <pre>
     * 1. Check format of decimal numbers in string fields in request, if invalid format
     *    found return response code -1
     * 2. Check if product in request exists, if yes verify that the credit parameters
     *    meet the product conditions. If any of the above is not fulfilled, return the
     *    code -2.
     * 3. Add agreement to table, return new agreement id
     * </pre>
     */
    @Override
    public void addAgreement(AgreementRequest request, StreamObserver<EngineResponse> responseObserver) {
        try {
            AgreementDto agreementDto = AgreementDto.fromRequest(request);
            OperationResult<Boolean> verification = validationService.checkAgreement(agreementDto);
            if (verification.getValue()) {
                OperationResult<Long> agreement = agreementCreationService.createAgreement(agreementDto);
                EngineResponse response = makeEngineResponse(agreement);
                responseObserver.onNext(response);
            } else {
                EngineResponse errorResponse = makeValidationErrorResponse(verification.getMessage());
                responseObserver.onNext(errorResponse);
            }
        } catch (NumberFormatException e) {
            responseObserver.onNext(AGREEMENT_FORMAT_CHECK_FAILED);
        }
        responseObserver.onCompleted();
    }

    /**
     * Processing stages:
     * <pre>
     * 1. Check format of date in string fields in request, if invalid format
     *    found return response code -1
     * 2. Check if specified agreement exists. If no, return the code -2.
     * 3. Activate agreement, create new payments schedule, and list of payments.
     *    Return created payment schedule id
     * </pre>
     */
    @Override
    public void disbursement(DisbursementRequest request, StreamObserver<EngineResponse> responseObserver) {
        try {
            DisbursementDto disbursementDto = DisbursementDto.fromRequest(request);
            OperationResult<Long> result = disbursementService.disburse(disbursementDto);
            EngineResponse response = makeEngineResponse(result);
            responseObserver.onNext(response);
        } catch (DateTimeParseException e) {
            responseObserver.onNext(DISBURSEMENT_FORMAT_CHECK_FAILED);
        }
        responseObserver.onCompleted();
    }

    private static EngineResponse makeEngineResponse(OperationResult<Long> agreementResult) {
        return makeEngineResponse(agreementResult.getValue(), agreementResult.getMessage());
    }

    private static EngineResponse makeEngineResponse(long value, String message) {
        return EngineResponse.newBuilder()
                .setValue(value)
                .setMessage(message)
                .build();
    }

    private static EngineResponse makeFormatCheckErrorResponse(String message) {
        return makeEngineResponse(FORMAT_CHECK_ERROR_CODE, message);
    }

    private static EngineResponse makeValidationErrorResponse(String message) {
        return makeEngineResponse(VALIDATION_ERROR_CODE, message);
    }
}

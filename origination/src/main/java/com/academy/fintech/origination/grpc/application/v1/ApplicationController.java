package com.academy.fintech.origination.grpc.application.v1;

import com.academy.fintech.application.ApplicationRequest;
import com.academy.fintech.application.ApplicationResponse;
import com.academy.fintech.application.ApplicationServiceGrpc;
import com.academy.fintech.application.CancelApplicationRequest;
import com.academy.fintech.application.CancelApplicationResponse;
import com.academy.fintech.origination.core.service.application.ApplicationManagementService;
import com.academy.fintech.origination.core.service.application.db.application.PaymentApplicationException;
import com.academy.fintech.origination.core.service.application.db.client.ClientException;
import com.academy.fintech.origination.core.service.scoring.ScheduledScoringService;
import com.academy.fintech.origination.grpc.application.v1.dto.ApplicationCreationDto;
import com.academy.fintech.origination.grpc.application.v1.dto.ApplicationScoringDto;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main controller of the origination service. Handles requests for creating and canceling applications.
 */
@Slf4j
@GRpcService
@RequiredArgsConstructor
public class ApplicationController extends ApplicationServiceGrpc.ApplicationServiceImplBase {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+@\\w+\\.\\w++");
    private static final Metadata.Key<String> APPLICATION_CREATION_KEY =
            Metadata.Key.of("agreement_id", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> CLIENT_CREATION_KEY =
            Metadata.Key.of("client_email", Metadata.ASCII_STRING_MARSHALLER);
    private final ApplicationManagementService applicationManagementService;
    private final ScheduledScoringService scheduledScoringService;

    /**
     * Verify the format, create a request, and return its ID.
     * <pre>
     * 1. In case of incorrect email format,
     * send an error with the status FAILED_PRECONDITION.
     * 2. If client data is entered incorrectly,
     * send an error with the status INVALID_ARGUMENT and include the problematic client's email in
     * Trailers.
     * 3. In case of a duplicate request, send an error with the status ALREADY_EXISTS and
     * include the request ID in Trailers.
     * </pre>
     */
    @Override
    public void create(ApplicationRequest request, StreamObserver<ApplicationResponse> responseObserver) {
        log.info("Got creation request: {}", request);
        try {
            ApplicationCreationDto dto = mapToDto(request);
            ApplicationScoringDto scoringDto = applicationManagementService.addApplication(dto);
            responseObserver.onNext(
                    ApplicationResponse.newBuilder()
                            .setApplicationId(scoringDto.applicationId())
                            .build()
            );
            responseObserver.onCompleted();
            scheduledScoringService.submit(scoringDto);
        } catch (ClientException e) {
            sendErrorImpl(Status.INVALID_ARGUMENT, CLIENT_CREATION_KEY, e.getClientEmail(), responseObserver);
        } catch (PaymentApplicationException e) {
            sendErrorImpl(Status.ALREADY_EXISTS, APPLICATION_CREATION_KEY, e.getApplicationId(), responseObserver);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
        }
    }

    /**
     * Send a request to cancel the application and return the cancellation result:
     * true if the application was successfully canceled, and
     * false if the application had a status other than New.
     * In case the application with the given ID does not exist, send an error
     * with the status NOT_FOUND.
     */
    @Override
    public void cancel(CancelApplicationRequest request, StreamObserver<CancelApplicationResponse> responseObserver) {
        log.info("Got cancellation request: {}", request);
        try {
            String applicationId = request.getApplicationId();
            responseObserver.onNext(
                    CancelApplicationResponse.newBuilder()
                            .setSuccess(applicationManagementService.cancelApplication(applicationId))
                            .build()
            );
            responseObserver.onCompleted();
        } catch (PaymentApplicationException e) {
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    private void sendErrorImpl(Status status, Metadata.Key<String> key, String message,
                               StreamObserver<ApplicationResponse> responseObserver) {
        Metadata metadata = new Metadata();
        metadata.put(key, message);
        responseObserver.onError(status.asRuntimeException(metadata));
    }

    /**
     * Create a DTO from the request and validate fields values.
     *
     * @throws IllegalArgumentException if email field has invalid format
     */
    private ApplicationCreationDto mapToDto(ApplicationRequest request) {
        Matcher matcher = EMAIL_PATTERN.matcher(request.getEmail());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid email: " + request.getEmail());
        }
        if (request.getSalary() < 1) {
            throw new IllegalArgumentException("Invalid salary: " + request.getSalary());
        }
        if (request.getDisbursementAmount() < 1) {
            throw new IllegalArgumentException("Invalid disbursement amount: " + request.getDisbursementAmount());
        }
        return new ApplicationCreationDto(request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                new BigDecimal(request.getSalary()),
                new BigDecimal(request.getDisbursementAmount()));
    }
}


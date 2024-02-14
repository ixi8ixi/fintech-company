package com.academy.fintech.api.core.origination.client;

import com.academy.fintech.api.core.origination.client.grpc.OriginationGrpcClient;
import com.academy.fintech.api.public_interface.application.dto.ApplicationDto;
import com.academy.fintech.application.ApplicationRequest;
import com.academy.fintech.application.ApplicationResponse;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OriginationClientService {

    private final OriginationGrpcClient originationGrpcClient;

    public String createApplication(ApplicationDto applicationDto) {
        ApplicationRequest request = mapDtoToRequest(applicationDto);

        try {
            ApplicationResponse response = originationGrpcClient.createApplication(request);
            return response.getApplicationId();
        } catch (StatusRuntimeException e) {
            Status status = e.getStatus();
            Metadata trailers = e.getTrailers();

            if (status == null || trailers == null) {
                throw e;
            }

            if (status.getCode() == Status.Code.INVALID_ARGUMENT) {
                String clientId = trailers.get(Metadata.Key.of(
                        "client_email", Metadata.ASCII_STRING_MARSHALLER));
                return "Incorrect client data from email: " + clientId;
            } else if (status.getCode() == Status.Code.ALREADY_EXISTS) {
                String agreementId = trailers.get(Metadata.Key.of(
                        "agreement_id", Metadata.ASCII_STRING_MARSHALLER));
                return "The application has already been created previously: " + agreementId;
            }
            throw e;
        }
    }

    private static ApplicationRequest mapDtoToRequest(ApplicationDto applicationDto) {
        return ApplicationRequest.newBuilder()
                .setFirstName(applicationDto.firstName())
                .setLastName(applicationDto.lastName())
                .setEmail(applicationDto.email())
                .setSalary(applicationDto.salary())
                .setDisbursementAmount(applicationDto.amount())
                .build();
    }

}

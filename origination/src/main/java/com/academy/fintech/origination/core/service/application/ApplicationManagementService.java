package com.academy.fintech.origination.core.service.application;

import com.academy.fintech.origination.core.service.application.db.application.PaymentApplication;
import com.academy.fintech.origination.core.service.application.db.application.PaymentApplicationException;
import com.academy.fintech.origination.core.service.application.db.application.PaymentApplicationService;
import com.academy.fintech.origination.core.service.application.db.client.Client;
import com.academy.fintech.origination.core.service.application.db.client.ClientService;
import com.academy.fintech.origination.grpc.application.v1.dto.ApplicationCreationDto;
import com.academy.fintech.origination.grpc.application.v1.dto.ApplicationScoringDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for overall management of customer databases and credit application requests.
 */
@Service
@RequiredArgsConstructor
public class ApplicationManagementService {
    private final PaymentApplicationService paymentApplicationService;
    private final ClientService clientService;

    /**
     * Create new application by given values with status new. Before application creation, check if client with given
     * parameters exist, and create if no.
     */
    public ApplicationScoringDto addApplication(ApplicationCreationDto dto) {
        Client client = mapClientDtoToEntity(dto);
        String clientId = clientService.findOrCreate(client);
        PaymentApplication application = mapPaymentApplicationDtoToEntity(clientId, dto);
        String applicationId = paymentApplicationService.createApplication(application);
        return new ApplicationScoringDto(applicationId, clientId, dto.salary(), dto.requestedDisbursementAmount());
    }

    /**
     * Check if a request with the specified identifier exists. If yes - cancel the request.
     *
     * @return true - if the request is found and has a status of New,
     * false - if the status of the found request is not New.
     * @throws PaymentApplicationException if application not found
     */
    public boolean cancelApplication(String applicationId) {
        return paymentApplicationService.setAndCheckStatus(
                applicationId,
                PaymentApplication.PaymentApplicationStatus.NEW,
                PaymentApplication.PaymentApplicationStatus.CANCELED
        );
    }

    private Client mapClientDtoToEntity(ApplicationCreationDto dto) {
        return Client.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .salary(dto.salary())
                .build();
    }

    private PaymentApplication mapPaymentApplicationDtoToEntity(String clientId, ApplicationCreationDto dto) {
        return PaymentApplication.builder()
                .clientId(clientId)
                .status(PaymentApplication.PaymentApplicationStatus.NEW)
                .requestedDisbursementAmount(dto.requestedDisbursementAmount())
                .build();
    }
}

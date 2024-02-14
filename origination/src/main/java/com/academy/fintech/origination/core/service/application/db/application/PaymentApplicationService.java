package com.academy.fintech.origination.core.service.application.db.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The service responsible for interacting with the database of credit opening requests.
 */
@Service
@RequiredArgsConstructor
public class PaymentApplicationService {
    private final PaymentApplicationRepository paymentApplicationRepository;

    /**
     * Check the request for duplicates, and if none exist, create a new request in the database
     * with the status NEW.
     *
     * @return The identifier of the newly created request.
     * @throws PaymentApplicationException if duplicate found
     */
    @Transactional
    public String createApplication(PaymentApplication application) {
        Optional<PaymentApplication> paymentApplicationCandidate =
                paymentApplicationRepository.findByClientIdAndStatusAndRequestedDisbursementAmount(
                application.getClientId(), application.getStatus(), application.getRequestedDisbursementAmount());
        if (paymentApplicationCandidate.isPresent()) {
            PaymentApplication paymentApplication = paymentApplicationCandidate.get();
            throw new PaymentApplicationException(paymentApplication.getId());
        }
        return paymentApplicationRepository.save(application).getId();
    }

    @Transactional
    public PaymentApplication.PaymentApplicationStatus setStatus(
            String applicationId,
            PaymentApplication.PaymentApplicationStatus expectedStatus,
            PaymentApplication.PaymentApplicationStatus newStatus) {
        Optional<PaymentApplication> paymentApplicationCandidate = paymentApplicationRepository.findById(applicationId);
        if (paymentApplicationCandidate.isEmpty()) {
            throw new PaymentApplicationException("Application: " + applicationId + " does not exist.");
        }
        PaymentApplication paymentApplication = paymentApplicationCandidate.get();
        PaymentApplication.PaymentApplicationStatus actualStatus = paymentApplication.getStatus();
        if (actualStatus != expectedStatus) {
            return actualStatus;
        }
        paymentApplication.setStatus(newStatus);
        paymentApplicationRepository.save(paymentApplication);
        return newStatus;
    }

    @Transactional
    public boolean setAndCheckStatus(
            String applicationId,
            PaymentApplication.PaymentApplicationStatus expectedStatus,
            PaymentApplication.PaymentApplicationStatus newStatus) {
        return setStatus(applicationId, expectedStatus, newStatus) == newStatus;
    }

    @Transactional
    public void setOrThrowStatus(
            String applicationId,
            PaymentApplication.PaymentApplicationStatus expectedStatus,
            PaymentApplication.PaymentApplicationStatus newStatus) {
        PaymentApplication.PaymentApplicationStatus resultStatus
                = setStatus(applicationId, expectedStatus, newStatus);
        if (resultStatus != newStatus) {
            throw new PaymentApplicationException("Application awaiting scoring must have status new, not: "
                    + resultStatus);
        }
    }
}

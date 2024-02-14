package com.academy.fintech.origination.core.service.application;

import com.academy.fintech.origination.core.service.application.db.application.PaymentApplication;
import com.academy.fintech.origination.core.service.application.db.application.PaymentApplicationService;
import com.academy.fintech.origination.core.service.application.db.application.PaymentApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for managing applications based on the results of the scoring stage.
 */
@Service
@RequiredArgsConstructor
public class ScoringApplicationService {
    private final PaymentApplicationService paymentApplicationService;

    /**
     * Set application status for {@link PaymentApplication.PaymentApplicationStatus#SCORING_ACCEPTED}
     * @throws PaymentApplicationException if application with given application id is not found or
     * status of found id isn't {@link PaymentApplication.PaymentApplicationStatus#NEW}
     */
    public void scoringAcceptApplication(String applicationId) {
        paymentApplicationService.setOrThrowStatus(
                applicationId,
                PaymentApplication.PaymentApplicationStatus.NEW,
                PaymentApplication.PaymentApplicationStatus.SCORING_ACCEPTED
        );
    }

    /**
     * Set application status for {@link PaymentApplication.PaymentApplicationStatus#SCORING_REJECTED}
     * @throws PaymentApplicationException if application with given application id is not found or
     * status of found id isn't {@link PaymentApplication.PaymentApplicationStatus#NEW}
     */
    public void scoringRejectApplication(String applicationId) {
        paymentApplicationService.setOrThrowStatus(
                applicationId,
                PaymentApplication.PaymentApplicationStatus.NEW,
                PaymentApplication.PaymentApplicationStatus.SCORING_REJECTED
        );
    }
}

package com.academy.fintech.pe.core.service.agreement.db.agreement;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgreementService {
    /**
     * Add given agreement to db and return its id.
     */
    long addAgreement(Agreement agreement);

    /**
     * Update agreement state with status `ACTIVATED`, disbursement and next payment dates.
     * If activation is successful, returns the updated agreement; otherwise, returns empty.
     */
    Optional<Agreement> activateAgreement(long agreementNumber, LocalDate disbursementDate, LocalDate nextPaymentDate);

    List<LocalDate> acceptedPaymentDates(long clientId);
}

package com.academy.fintech.origination.core.service.application.db.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface PaymentApplicationRepository extends JpaRepository<PaymentApplication, String> {
    Optional<PaymentApplication> findByClientIdAndStatusAndRequestedDisbursementAmount(
            String clientId, PaymentApplication.PaymentApplicationStatus status,
            BigDecimal requestedDisbursementAmount);
}

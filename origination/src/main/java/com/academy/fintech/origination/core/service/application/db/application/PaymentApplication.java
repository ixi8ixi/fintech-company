package com.academy.fintech.origination.core.service.application.db.application;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "applications")
public class PaymentApplication {
    public enum PaymentApplicationStatus {
        /**
         * The newly created request is awaiting approval.
         */
        NEW,

        /**
         * The request has been canceled by the user and closed.
         */
        CANCELED,

        /**
         * The request has received a positive response from the scoring service.
         */
        SCORING_ACCEPTED,

        /**
         * The request has received a negative response from the scoring service.
         */
        SCORING_REJECTED,

        /**
         * The request has been accepted and is awaiting payment.
         */
        ACTIVE,

        /**
         * The request has been paid for and closed.
         */
        CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String clientId;
    private BigDecimal requestedDisbursementAmount;
    @Enumerated(EnumType.STRING)
    private PaymentApplicationStatus status;
}

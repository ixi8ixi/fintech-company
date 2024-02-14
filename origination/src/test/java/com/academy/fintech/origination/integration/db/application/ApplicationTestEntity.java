package com.academy.fintech.origination.integration.db.application;

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
public class ApplicationTestEntity {
    public enum PaymentApplicationStatus {
        NEW, SCORING, ACCEPTED, ACTIVE, CLOSED, DENIED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String clientId;
    private BigDecimal requestedDisbursementAmount;
    @Enumerated(EnumType.STRING)
    private PaymentApplicationStatus status;
}

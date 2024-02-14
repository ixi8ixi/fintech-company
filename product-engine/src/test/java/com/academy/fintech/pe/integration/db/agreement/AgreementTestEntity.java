package com.academy.fintech.pe.integration.db.agreement;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "agreements")
public class AgreementTestEntity {
    public enum AgreementStatus {
        PENDING, ACCEPTED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreementId;
    private Long clientId;
    private Integer loanTerm;
    private BigDecimal disbursementAmount;
    private BigDecimal principalAmount;
    private BigDecimal interest;
    private String productCode;
    @Enumerated(EnumType.STRING)
    private AgreementStatus status;
    private LocalDate disbursementDate;
    private LocalDate nextPaymentDate;
}

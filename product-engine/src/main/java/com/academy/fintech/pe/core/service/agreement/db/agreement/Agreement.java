package com.academy.fintech.pe.core.service.agreement.db.agreement;

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
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "agreements")
public class Agreement {
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

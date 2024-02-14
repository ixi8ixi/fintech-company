package com.academy.fintech.pe.core.service.agreement.db.payment;

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

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Table(name = "schedule_payments")
public class Payment {
    public enum PaymentStatus {
        PENDING, PAID, OUTDATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private Long scheduleId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private LocalDate paymentDate;
    private BigDecimal periodPayment;
    private BigDecimal interestPayment;
    private BigDecimal principalPayment;
    private Integer periodNumber;
}

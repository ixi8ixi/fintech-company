package com.academy.fintech.pe.integration.db.payment;

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
@Table(name = "schedule_payments")
public class PaymentTestEntity {
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

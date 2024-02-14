package com.academy.fintech.pe.integration.db.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTestRepository extends JpaRepository<PaymentTestEntity, Long> {
    List<PaymentTestEntity> findByScheduleId(Long scheduleId);
}

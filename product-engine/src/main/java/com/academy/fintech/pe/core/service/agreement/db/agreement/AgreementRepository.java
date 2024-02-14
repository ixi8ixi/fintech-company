package com.academy.fintech.pe.core.service.agreement.db.agreement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    @Query("SELECT a.nextPaymentDate FROM Agreement a WHERE a.status = :status AND a.clientId = :clientId")
    List<LocalDate> paymentDatesByClientId(long clientId, Agreement.AgreementStatus status);
}

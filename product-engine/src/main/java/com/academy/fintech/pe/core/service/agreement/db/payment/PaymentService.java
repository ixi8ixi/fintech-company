package com.academy.fintech.pe.core.service.agreement.db.payment;

import java.util.List;

public interface PaymentService {
    void addPayment(Payment payment);
    void addPayments(List<Payment> paymentList);
}

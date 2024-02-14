package com.academy.fintech.pe.core.service.agreement.db.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public void addPayment(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public void addPayments(List<Payment> paymentList) {
        paymentRepository.saveAll(paymentList);
    }
}

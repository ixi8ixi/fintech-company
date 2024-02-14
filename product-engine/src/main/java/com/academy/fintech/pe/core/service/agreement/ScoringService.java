package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.agreement.AgreementService;
import com.academy.fintech.pe.core.service.agreement.utils.LoanUtils;
import com.academy.fintech.pe.grpc.scoring.v1.dto.ScoringDto;
import com.academy.fintech.pe.grpc.scoring.v1.dto.ScoringResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringService {
    private final AgreementService agreementService;

    public ScoringResponseDto score(ScoringDto dto) {
        BigDecimal monthlyInterestRate = LoanUtils.countMonthlyInterestRate(dto.interestRate());
        BigDecimal pmt = LoanUtils.fixedMonthlyPayment(dto.loanTerm(), dto.requestedDisbursementAmount(),
                monthlyInterestRate);
        List<LocalDate> paymentDates = agreementService.acceptedPaymentDates(Long.parseLong(dto.clientId()));
        return new ScoringResponseDto(pmt, paymentDates);
    }
}

package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.agreement.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.agreement.AgreementService;
import com.academy.fintech.pe.core.service.agreement.operation.result.OperationResult;
import com.academy.fintech.pe.grpc.agreement.v1.dto.AgreementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgreementCreationService {
    private final AgreementService agreementService;

    public OperationResult<Long> createAgreement(AgreementDto agreementDto) {
        Agreement agreement = Agreement.builder()
                .status(Agreement.AgreementStatus.PENDING)
                .clientId(agreementDto.getClientId())
                .loanTerm(agreementDto.getLoanTerm())
                .disbursementAmount(agreementDto.getDisbursementAmount())
                .principalAmount(agreementDto.getPrincipalAmount())
                .interest(agreementDto.getInterest())
                .productCode(agreementDto.getProductId())
                .build();

        return OperationResult.of(agreementService.addAgreement(agreement));
    }
}

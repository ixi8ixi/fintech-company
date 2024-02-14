package com.academy.fintech.pe.grpc.agreement.v1.dto;

import com.academy.fintech.pe.grpc.agreement.v1.AgreementRequest;
import lombok.Getter;

import java.math.BigDecimal;

public class AgreementDto {
    private final AgreementRequest agreementRequest;
    @Getter
    private final BigDecimal disbursementAmount;
    @Getter
    private final BigDecimal originationAmount;
    @Getter
    private final BigDecimal interest;
    @Getter
    private final BigDecimal principalAmount;

    private AgreementDto(AgreementRequest agreementRequest) {
        this.agreementRequest = agreementRequest;
        interest = new BigDecimal(agreementRequest.getInterest());
        originationAmount = new BigDecimal(agreementRequest.getOriginationAmount());
        disbursementAmount = new BigDecimal(agreementRequest.getDisbursementAmount());
        principalAmount = getOriginationAmount().add(getDisbursementAmount());
    }

    /**
     * Create new Agreement data object from request.
     *
     * @throws NumberFormatException if request contains numbers in invalid format
     */
    public static AgreementDto fromRequest(AgreementRequest agreementRequest) {
        return new AgreementDto(agreementRequest);
    }

    public long getClientId() {
        return agreementRequest.getClientId();
    }

    public int getLoanTerm() {
        return agreementRequest.getLoanTerm();
    }

    public String getProductId() {
        return agreementRequest.getProductId();
    }

}

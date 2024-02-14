package com.academy.fintech.pe.grpc.agreement.v1.dto;

import com.academy.fintech.pe.grpc.agreement.v1.DisbursementRequest;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Getter
public class DisbursementDto {
    private final LocalDate disbursementDate;
    private final long agreementId;

    private DisbursementDto(DisbursementRequest disbursementRequest) {
        this.disbursementDate = LocalDate.parse(disbursementRequest.getDate());
        this.agreementId = disbursementRequest.getAgreementNumber();
    }

    /**
     * Create new Disbursement data object from request.
     *
     * @throws DateTimeParseException if request contains invalid date
     */
    public static DisbursementDto fromRequest(DisbursementRequest disbursementRequest) {
        return new DisbursementDto(disbursementRequest);
    }
}

package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.agreement.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.agreement.AgreementService;
import com.academy.fintech.pe.core.service.agreement.db.payment.Payment;
import com.academy.fintech.pe.core.service.agreement.db.payment.PaymentService;
import com.academy.fintech.pe.core.service.agreement.db.schedule.Schedule;
import com.academy.fintech.pe.core.service.agreement.db.schedule.ScheduleService;
import com.academy.fintech.pe.core.service.agreement.operation.result.OperationResult;
import com.academy.fintech.pe.core.service.agreement.utils.LoanUtils;
import com.academy.fintech.pe.grpc.agreement.v1.dto.DisbursementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DisbursementService {
    private static final long ERROR_CODE = -3;
    private final AgreementService agreementService;
    private final ScheduleService scheduleService;
    private final PaymentService paymentService;

    /**
     * Activate given agreement and create payment schedule and all payments for it.
     * If no such agreement return -3.
     */
    public OperationResult<Long> disburse(DisbursementDto disbursementDto) {
        LocalDate disbursementDate = disbursementDto.getDisbursementDate();
        LocalDate firstPaymentDate = LoanUtils.firstPaymentDate(disbursementDate);
        Optional<Agreement> agreementCandidate = agreementService.activateAgreement(disbursementDto.getAgreementId(),
                disbursementDate, firstPaymentDate);

        if (agreementCandidate.isEmpty()) {
            return OperationResult.of(ERROR_CODE, "NO SUCH AGREEMENT");
        }

        Agreement agreement = agreementCandidate.get();
        Schedule schedule = Schedule.builder()
                .agreementId(agreement.getAgreementId())
                .scheduleVersion(1)
                .build();

        long scheduleId = scheduleService.addSchedule(schedule);
        List<Payment> paymentList = LoanUtils.createPayments(agreement, firstPaymentDate, scheduleId);
        paymentService.addPayments(paymentList);
        return OperationResult.of(scheduleId);
    }
}

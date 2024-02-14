package com.academy.fintech.pe.integration;

import com.academy.fintech.pe.integration.config.DBSetupExtension;
import com.academy.fintech.pe.integration.config.DBTestConfiguration;
import com.academy.fintech.pe.integration.db.agreement.AgreementTestEntity;
import com.academy.fintech.pe.integration.db.agreement.AgreementTestRepository;
import com.academy.fintech.pe.integration.db.payment.PaymentTestEntity;
import com.academy.fintech.pe.integration.db.payment.PaymentTestRepository;
import com.academy.fintech.pe.integration.db.schedule.ScheduleTestEntity;
import com.academy.fintech.pe.integration.db.schedule.ScheduleTestRepository;
import com.academy.fintech.pe.grpc.agreement.v1.AgreementRequest;
import com.academy.fintech.pe.grpc.agreement.v1.AgreementServiceGrpc;
import com.academy.fintech.pe.grpc.agreement.v1.DisbursementRequest;
import com.academy.fintech.pe.grpc.agreement.v1.EngineResponse;
import com.academy.fintech.pe.utils.GrpcTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@ExtendWith(DBSetupExtension.class)
@Import(DBTestConfiguration.class)
public class SchedulePaymentTest {
    private static final String SAMPLE_DATE_STRING = "2000-01-01";
    private static final LocalDate SAMPLE_DATE = LocalDate.parse(SAMPLE_DATE_STRING);
    private static final AgreementRequest SAMPLE_AGREEMENT_REQUEST = AgreementRequest.newBuilder()
            .setClientId(123456)
            .setDisbursementAmount("400000")
            .setInterest("10.1")
            .setLoanTerm(13)
            .setOriginationAmount("5000")
            .setProductId("CL1.0.0")
            .build();

    @Autowired
    private AgreementServiceGrpc.AgreementServiceBlockingStub stub;
    @Autowired
    private AgreementTestRepository agreementTestRepository;
    @Autowired
    private ScheduleTestRepository scheduleTestRepository;
    @Autowired
    private PaymentTestRepository paymentTestRepository;

    @BeforeEach
    public void clearTables() {
        agreementTestRepository.deleteAll();
        scheduleTestRepository.deleteAll();
        paymentTestRepository.deleteAll();
    }

    @Test
    public void schedule_table_empty_if_no_agreements_activated() {
        for (int i = 0; i < 3; i++) {
            GrpcTestUtils.sendRandomRequest(stub);
        }
        Assertions.assertEquals(0, scheduleTestRepository.count());
    }

    @Test
    public void activate_one() {
        long agreementId = stub.addAgreement(SAMPLE_AGREEMENT_REQUEST).getValue();
        DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                .setAgreementNumber(agreementId)
                .setDate(SAMPLE_DATE_STRING)
                .build();
        long scheduleId = stub.disbursement(disbursementRequest).getValue();
        Assertions.assertTrue(scheduleId > 0);
        Optional<ScheduleTestEntity> scheduleCandidate = scheduleTestRepository.findById(scheduleId);
        Assertions.assertTrue(scheduleCandidate.isPresent());
        ScheduleTestEntity schedule = scheduleCandidate.get();

        Assertions.assertEquals(agreementId, schedule.getAgreementId());
        Assertions.assertEquals(1, schedule.getScheduleVersion());

        checkAgreementActivated(agreementId, SAMPLE_DATE);
    }

    @Test
    public void activate_one_and_check_payments() {
        checkRandomRequest();
    }

    @Test
    public void cannot_activate_not_existent_agreement() {
        DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                .setAgreementNumber(123)
                .setDate("1999-01-01")
                .build();
        EngineResponse response = stub.disbursement(disbursementRequest);
        GrpcTestUtils.assertProcessingError(response.getValue());
    }

    @Test
    public void cannot_activate_agreement_with_invalid_date_format() {
        long agreementId = GrpcTestUtils.sendRandomRequest(stub).getValue();
        DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                .setAgreementNumber(agreementId)
                .setDate("2023-32-02")
                .build();
        EngineResponse response = stub.disbursement(disbursementRequest);
        GrpcTestUtils.assertValidationError(response.getValue());
    }

    @Test
    public void activate_a_lot_of_payments() {
        int totalPayments = 0;
        for (int i = 0; i < 100; i++) {
            totalPayments += checkRandomRequest();
        }
        Assertions.assertEquals(totalPayments, paymentTestRepository.count());
    }

    private void checkAgreementActivated(long agreementId, LocalDate disbursementDate) {
        Optional<AgreementTestEntity> agreementCandidate = agreementTestRepository.findById(agreementId);
        Assertions.assertTrue(agreementCandidate.isPresent());
        AgreementTestEntity agreement = agreementCandidate.get();

        Assertions.assertEquals(AgreementTestEntity.AgreementStatus.ACCEPTED, agreement.getStatus());
        Assertions.assertEquals(disbursementDate, agreement.getDisbursementDate());
        Assertions.assertEquals(disbursementDate.plusMonths(1), agreement.getNextPaymentDate());
    }

    private int checkRandomRequest() {
        AgreementRequest request = GrpcTestUtils.makeRandomValidRequest();
        int loanTerm = request.getLoanTerm();
        EngineResponse agreementCreationResponse = stub.addAgreement(request);
        long agreementId = agreementCreationResponse.getValue();
        DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                .setDate(SAMPLE_DATE_STRING)
                .setAgreementNumber(agreementId)
                .build();

        long scheduleId = stub.disbursement(disbursementRequest).getValue();
        List<PaymentTestEntity> allPayments = paymentTestRepository.findByScheduleId(scheduleId);
        checkAgreementActivated(agreementId, SAMPLE_DATE);
        checkPayments(allPayments, scheduleId, SAMPLE_DATE, loanTerm);
        return allPayments.size();
    }

    private static void checkPayments(List<PaymentTestEntity> payments, Long scheduleId,
                                         LocalDate disbursementDate, int loanTerm) {
        Set<LocalDate> allDates = GrpcTestUtils.allPaymentDates(disbursementDate, loanTerm);
        Set<Integer> periodNumbers = IntStream.range(1, loanTerm + 1).boxed().collect(Collectors.toSet());

        for (PaymentTestEntity entity : payments) {
            Assertions.assertEquals(scheduleId, entity.getScheduleId());
            Assertions.assertEquals(PaymentTestEntity.PaymentStatus.PENDING, entity.getStatus());
            int periodNumber = entity.getPeriodNumber();
            LocalDate date = entity.getPaymentDate();
            Assertions.assertTrue(periodNumbers.contains(periodNumber));
            Assertions.assertTrue(allDates.contains(date));
            periodNumbers.remove(periodNumber);
            allDates.remove(date);
        }
    }
}

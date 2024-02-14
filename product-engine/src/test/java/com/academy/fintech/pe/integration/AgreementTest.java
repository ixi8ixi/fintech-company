package com.academy.fintech.pe.integration;

import com.academy.fintech.pe.integration.config.DBSetupExtension;
import com.academy.fintech.pe.integration.config.DBTestConfiguration;
import com.academy.fintech.pe.integration.db.agreement.AgreementTestEntity;
import com.academy.fintech.pe.integration.db.agreement.AgreementTestRepository;
import com.academy.fintech.pe.integration.db.product.ExpectedProducts;
import com.academy.fintech.pe.grpc.agreement.v1.AgreementRequest;
import com.academy.fintech.pe.grpc.agreement.v1.AgreementServiceGrpc;
import com.academy.fintech.pe.grpc.agreement.v1.EngineResponse;
import com.academy.fintech.pe.utils.GrpcTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
@ExtendWith(DBSetupExtension.class)
@Import(DBTestConfiguration.class)
public class AgreementTest {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    @Autowired
    private AgreementServiceGrpc.AgreementServiceBlockingStub stub;
    @Autowired
    private AgreementTestRepository agreementTestRepository;

    @BeforeEach
    public void clearTable() {
        agreementTestRepository.deleteAll();
    }

    @Test
    public void add_one_agreement() {
        EngineResponse response = sendRandomAgreement();
        Assertions.assertEquals(1, agreementTestRepository.count());
        Optional<AgreementTestEntity> agreementCandidate = agreementTestRepository.findById(response.getValue());
        Assertions.assertTrue(agreementCandidate.isPresent());
        AgreementTestEntity agreement = agreementCandidate.get();
        Assertions.assertTrue(ExpectedProducts.inBounds(ExpectedProducts.CASH_LOAN,
                agreement.getLoanTerm(),
                agreement.getPrincipalAmount(),
                agreement.getInterest(),
                agreement.getPrincipalAmount().subtract(agreement.getDisbursementAmount())));
    }

    @Test
    public void new_contract_must_be_pending() {
        EngineResponse response = sendRandomAgreement();
        Optional<AgreementTestEntity> agreementCandidate = agreementTestRepository.findById(response.getValue());
        Assertions.assertTrue(agreementCandidate.isPresent());
        AgreementTestEntity newAgreement = agreementCandidate.get();
        Assertions.assertEquals(AgreementTestEntity.AgreementStatus.PENDING, newAgreement.getStatus());
        Assertions.assertNull(newAgreement.getDisbursementDate());
        Assertions.assertNull(newAgreement.getNextPaymentDate());
    }

    @Test
    public void cannot_add_agreement_with_invalid_number_format() {
        AgreementRequest invalidInterest = customInterest("INVALID INTEREST");
        EngineResponse invalidInterestResponse = stub.addAgreement(invalidInterest);
        GrpcTestUtils.assertValidationError(invalidInterestResponse.getValue());
        Assertions.assertEquals(0, agreementTestRepository.count());

        AgreementRequest invalidDisbursementAmount = AgreementRequest
                .newBuilder()
                .setClientId(RANDOM.nextLong())
                .setDisbursementAmount("INVALID DISBURSEMENT AMOUNT")
                .setInterest(Double.toString(RANDOM.nextDouble(8.0, 15.0)))
                .setLoanTerm(32)
                .setOriginationAmount(Long.toString(RANDOM.nextLong(2000, 10000)))
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidDisbursementResponse = stub.addAgreement(invalidDisbursementAmount);
        GrpcTestUtils.assertValidationError(invalidDisbursementResponse.getValue());
        Assertions.assertEquals(0, agreementTestRepository.count());

        AgreementRequest invalidOriginationAmount = AgreementRequest
                .newBuilder()
                .setClientId(RANDOM.nextLong())
                .setDisbursementAmount(Long.toString(RANDOM.nextLong(50000, 400001)))
                .setInterest(Double.toString(RANDOM.nextDouble(8.0, 15.0)))
                .setLoanTerm(32)
                .setOriginationAmount("INVALID ORIGINATION AMOUNT")
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidOriginationResponse = stub.addAgreement(invalidOriginationAmount);
        GrpcTestUtils.assertValidationError(invalidOriginationResponse.getValue());
        Assertions.assertEquals(0, agreementTestRepository.count());
    }

    @Test
    public void cannot_add_agreements_with_arguments_out_of_bounds() {
        AgreementRequest invalidPrincipalAmount = AgreementRequest
                .newBuilder()
                .setClientId(RANDOM.nextLong())
                .setDisbursementAmount("1000000")
                .setInterest(Double.toString(RANDOM.nextDouble(8.0, 15.0)))
                .setLoanTerm(RANDOM.nextInt(3, 25))
                .setOriginationAmount("1000000")
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidPrincipalAmountResponse = stub.addAgreement(invalidPrincipalAmount);
        GrpcTestUtils.assertVerificationError(invalidPrincipalAmountResponse.getValue());
        Assertions.assertEquals(0, agreementTestRepository.count());

        AgreementRequest invalidInterest = customInterest("22.22");
        EngineResponse invalidInterestResponse = stub.addAgreement(invalidInterest);
        GrpcTestUtils.assertVerificationError(invalidInterestResponse.getValue());
        Assertions.assertEquals(0, agreementTestRepository.count());

        AgreementRequest invalidLoanTerm = AgreementRequest
                .newBuilder()
                .setClientId(RANDOM.nextLong())
                .setDisbursementAmount(Long.toString(RANDOM.nextLong(50000, 400001)))
                .setInterest(Double.toString(RANDOM.nextDouble(8.0, 15.0)))
                .setLoanTerm(32)
                .setOriginationAmount(Long.toString(RANDOM.nextLong(2000, 10000)))
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidLoanTermResponse = stub.addAgreement(invalidLoanTerm);
        GrpcTestUtils.assertVerificationError(invalidLoanTermResponse.getValue());
        Assertions.assertEquals(0, agreementTestRepository.count());
    }

    @Test
    public void add_after_invalid() {
        AgreementRequest invalidPrincipalAmount = customInterest("100.0");
        EngineResponse invalidPrincipalAmountResponse = stub.addAgreement(invalidPrincipalAmount);
        GrpcTestUtils.assertVerificationError(invalidPrincipalAmountResponse.getValue());
        Assertions.assertEquals(0, agreementTestRepository.count());

        EngineResponse response = sendRandomAgreement();
        Optional<AgreementTestEntity> agreementCandidate = agreementTestRepository.findById(response.getValue());
        Assertions.assertTrue(agreementCandidate.isPresent());
        AgreementTestEntity agreement = agreementCandidate.get();
        Assertions.assertTrue(ExpectedProducts.inBounds(ExpectedProducts.CASH_LOAN,
                agreement.getLoanTerm(),
                agreement.getPrincipalAmount(),
                agreement.getInterest(),
                agreement.getPrincipalAmount().subtract(agreement.getDisbursementAmount())));
    }

    @Test
    public void add_a_lot_of_agreements() {
        for (int i = 0; i < 1000; i++) {
            sendRandomAgreement();
        }
        Assertions.assertEquals(1000, agreementTestRepository.count());
    }

    private EngineResponse sendRandomAgreement() {
        return stub.addAgreement(GrpcTestUtils.makeRandomValidRequest());
    }

    private AgreementRequest customInterest(String interestValue) {
        return AgreementRequest
                .newBuilder()
                .setClientId(RANDOM.nextLong())
                .setDisbursementAmount(Long.toString(RANDOM.nextLong(50000, 400001)))
                .setInterest(interestValue)
                .setLoanTerm(RANDOM.nextInt(3, 25))
                .setOriginationAmount(Long.toString(RANDOM.nextLong(2000, 10000)))
                .setProductId("CL1.0.0")
                .build();
    }
}

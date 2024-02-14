package com.academy.fintech.pe.blackbox;

import com.academy.fintech.pe.blackbox.containers.Containers;

import com.academy.fintech.pe.grpc.agreement.v1.AgreementRequest;
import com.academy.fintech.pe.grpc.agreement.v1.AgreementServiceGrpc;
import com.academy.fintech.pe.grpc.agreement.v1.DisbursementRequest;
import com.academy.fintech.pe.grpc.agreement.v1.EngineResponse;
import com.academy.fintech.pe.utils.GrpcTestUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GrpcTests {
    // Same sequence between starts
    private static final long SEED = 41560976151707912L;
    private static Random random;

    private static ManagedChannel channel;
    private static AgreementServiceGrpc.AgreementServiceBlockingStub stub;
    private static Containers containers;

    @BeforeAll
    public static void beforeAll() {
        random = new Random(SEED);
    }

    @BeforeEach
    public void setUp() {
        containers = new Containers();
        containers.start();
        channel = ManagedChannelBuilder
                .forAddress("localhost", containers.getGrpcPort())
                .usePlaintext()
                .build();
        stub = AgreementServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        containers.stop();
        channel.shutdownNow().awaitTermination(1, TimeUnit.DAYS);
    }

    @Test
    public void one_agreement() {
        EngineResponse response = stub.addAgreement(GrpcTestUtils.makeRandomValidRequest());
        GrpcTestUtils.assertSuccess(response.getValue());
    }

    @Test
    public void cannot_create_agreement_with_invalid_number_format() {
        AgreementRequest invalidDisbursementAmount = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount("INVALID DISBURSEMENT AMOUNT")
                .setInterest("12.12")
                .setLoanTerm(18)
                .setOriginationAmount("5000")
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidDisbursementAmountResponse = stub.addAgreement(invalidDisbursementAmount);
        GrpcTestUtils.assertValidationError(invalidDisbursementAmountResponse.getValue());

        AgreementRequest invalidInterest = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount("400000")
                .setInterest("INVALID INTEREST")
                .setLoanTerm(18)
                .setOriginationAmount("5000")
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidInterestResponse = stub.addAgreement(invalidInterest);
        GrpcTestUtils.assertValidationError(invalidInterestResponse.getValue());

        AgreementRequest invalidOrigination = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount("400000")
                .setInterest("12.12")
                .setLoanTerm(18)
                .setOriginationAmount("INVALID ORIGINATION AMOUNT")
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidOriginationResponse = stub.addAgreement(invalidOrigination);
        GrpcTestUtils.assertValidationError(invalidOriginationResponse.getValue());
    }

    @Test
    public void cannot_create_agreement_with_invalid_product() {
        AgreementRequest invalidPrincipalAmount = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount(Long.toString(random.nextLong(50000, 400001)))
                .setInterest(Double.toString(random.nextDouble(8.0, 15.0)))
                .setLoanTerm(random.nextInt(3, 25))
                .setOriginationAmount(Long.toString(random.nextLong(2000, 10000)))
                .setProductId("NO_SUCH_PRODUCT")
                .build();
        EngineResponse invalidPrincipalAmountResponse = stub.addAgreement(invalidPrincipalAmount);
        GrpcTestUtils.assertVerificationError(invalidPrincipalAmountResponse.getValue());
    }

    @Test
    public void agreements_with_parameters_out_of_bounds_should_fails() {
        AgreementRequest invalidPrincipalAmount = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount("1000000")
                .setInterest(Double.toString(random.nextDouble(8.0, 15.0)))
                .setLoanTerm(random.nextInt(3, 25))
                .setOriginationAmount("1000000")
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidPrincipalAmountResponse = stub.addAgreement(invalidPrincipalAmount);
        GrpcTestUtils.assertVerificationError(invalidPrincipalAmountResponse.getValue());

        AgreementRequest invalidInterest = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount(Long.toString(random.nextLong(50000, 400001)))
                .setInterest("22.22")
                .setLoanTerm(random.nextInt(3, 25))
                .setOriginationAmount(Long.toString(random.nextLong(2000, 10000)))
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidInterestResponse = stub.addAgreement(invalidInterest);
        GrpcTestUtils.assertVerificationError(invalidInterestResponse.getValue());

        AgreementRequest invalidLoanTerm = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount(Long.toString(random.nextLong(50000, 400001)))
                .setInterest(Double.toString(random.nextDouble(8.0, 15.0)))
                .setLoanTerm(32)
                .setOriginationAmount(Long.toString(random.nextLong(2000, 10000)))
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidLoanTermResponse = stub.addAgreement(invalidLoanTerm);
        GrpcTestUtils.assertVerificationError(invalidLoanTermResponse.getValue());
    }

    @Test
    public void correct_after_incorrect() {
        AgreementRequest invalidInterest = AgreementRequest
                .newBuilder()
                .setClientId(random.nextLong())
                .setDisbursementAmount(Long.toString(random.nextLong(50000, 400001)))
                .setInterest("22.22")
                .setLoanTerm(random.nextInt(3, 25))
                .setOriginationAmount(Long.toString(random.nextLong(2000, 10000)))
                .setProductId("CL1.0.0")
                .build();
        EngineResponse invalidInterestResponse = stub.addAgreement(invalidInterest);
        GrpcTestUtils.assertVerificationError(invalidInterestResponse.getValue());

        AgreementRequest correctRequest = GrpcTestUtils.makeRandomValidRequest();
        GrpcTestUtils.assertSuccess(stub.addAgreement(correctRequest).getValue());
    }

    @Test
    public void all_agreement_numbers_should_be_unique() {
        Set<Long> agreements = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            EngineResponse response = stub.addAgreement(GrpcTestUtils.makeRandomValidRequest());
            long agreementNumber = response.getValue();
            Assertions.assertFalse(agreements.contains(agreementNumber));
            agreements.add(agreementNumber);
        }
    }

    @Test
    public void activate_one() {
        EngineResponse response = stub.addAgreement(GrpcTestUtils.makeRandomValidRequest());
        DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                .setAgreementNumber(response.getValue())
                .setDate("2011-11-02")
                .build();
        EngineResponse disbursementResponse = stub.disbursement(disbursementRequest);
        GrpcTestUtils.assertSuccess(disbursementResponse.getValue());
    }

    @Test
    public void cannot_activate_non_existed_agreement() {
        // No agreements have been created, so agreement 123 doesn't exist
        DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                .setAgreementNumber(123)
                .setDate("2011-11-02")
                .build();
        EngineResponse disbursementResponse = stub.disbursement(disbursementRequest);
        GrpcTestUtils.assertFailure(disbursementResponse.getValue());
    }

    @Test
    public void cannot_activate_one_agreement_twice() {
        EngineResponse response = stub.addAgreement(GrpcTestUtils.makeRandomValidRequest());
        DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                .setAgreementNumber(response.getValue())
                .setDate("2011-11-02")
                .build();
        EngineResponse disbursementResponseFirst = stub.disbursement(disbursementRequest);
        GrpcTestUtils.assertSuccess(disbursementResponseFirst.getValue());
        EngineResponse disbursementResponseSecond = stub.disbursement(disbursementRequest);
        GrpcTestUtils.assertFailure(disbursementResponseSecond.getValue());
    }

    @Test
    public void all_schedule_id_should_be_unique() {
        Set<Long> agreements = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            EngineResponse response = stub.addAgreement(GrpcTestUtils.makeRandomValidRequest());
            long agreementNumber = response.getValue();
            agreements.add(agreementNumber);
        }

        Set<Long> schedules = new HashSet<>();
        agreements.forEach(agreement -> {
            DisbursementRequest disbursementRequest = DisbursementRequest.newBuilder()
                    .setAgreementNumber(agreement)
                    .setDate("2011-11-02")
                    .build();
            long scheduleId = stub.disbursement(disbursementRequest).getValue();
            Assertions.assertFalse(schedules.contains(scheduleId));
            schedules.add(scheduleId);
        });
    }
}

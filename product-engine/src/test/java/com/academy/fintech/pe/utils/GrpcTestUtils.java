package com.academy.fintech.pe.utils;

import com.academy.fintech.pe.grpc.agreement.v1.AgreementRequest;
import com.academy.fintech.pe.grpc.agreement.v1.AgreementServiceGrpc;
import com.academy.fintech.pe.grpc.agreement.v1.EngineResponse;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GrpcTestUtils {
    private static final Random RANDOM = new Random(4280428740247099084L);
    private GrpcTestUtils() {
        // No instance
        throw new AssertionError();
    }

    public static void assertSuccess(long response) {
        Assertions.assertTrue(response > 0);
    }

    public static void assertFailure(long response) {
        Assertions.assertEquals(-3, response);
    }

    public static void assertValidationError(long response) {
        Assertions.assertEquals(-1, response);
    }

    public static void assertVerificationError(long response) {
        Assertions.assertEquals(-2, response);
    }

    public static void assertProcessingError(long response) {
        Assertions.assertTrue(response < 2);
    }

    public static AgreementRequest makeRandomValidRequest() {
        return AgreementRequest
                .newBuilder()
                .setClientId(RANDOM.nextLong())
                .setDisbursementAmount(Long.toString(RANDOM.nextLong(50000, 400001)))
                .setInterest(Double.toString(RANDOM.nextDouble(8.0, 15.0)))
                .setLoanTerm(RANDOM.nextInt(3, 25))
                .setOriginationAmount(Long.toString(RANDOM.nextLong(2000, 10000)))
                .setProductId("CL1.0.0")
                .build();
    }

    public static EngineResponse sendRandomRequest(AgreementServiceGrpc.AgreementServiceBlockingStub stub) {
        return stub.addAgreement(makeRandomValidRequest());
    }

    public static Set<LocalDate> allPaymentDates(LocalDate disbursementDate, int loanTerm) {
        Set<LocalDate> result = new HashSet<>();
        for (int i = 1; i <= loanTerm; i++) {
            result.add(disbursementDate.plusMonths(i));
        }
        return result;
    }
}

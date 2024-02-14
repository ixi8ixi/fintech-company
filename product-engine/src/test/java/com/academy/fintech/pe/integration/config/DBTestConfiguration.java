package com.academy.fintech.pe.integration.config;

import com.academy.fintech.pe.grpc.agreement.v1.AgreementServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DBTestConfiguration {
    private final AgreementServiceGrpc.AgreementServiceBlockingStub blockingStub;

    public DBTestConfiguration() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        this.blockingStub = AgreementServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public AgreementServiceGrpc.AgreementServiceBlockingStub agreementServiceBlockingStub() {
        return blockingStub;
    }
}

package com.academy.fintech.origination.integration.config;

import com.academy.fintech.application.ApplicationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DBTestConfiguration {
    private final ApplicationServiceGrpc.ApplicationServiceBlockingStub stub;

    public DBTestConfiguration() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9094)
                .usePlaintext()
                .build();
        this.stub = ApplicationServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public ApplicationServiceGrpc.ApplicationServiceBlockingStub agreementServiceBlockingStub() {
        return stub;
    }
}

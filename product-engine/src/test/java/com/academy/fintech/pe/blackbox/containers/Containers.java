package com.academy.fintech.pe.blackbox.containers;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.List;

public class Containers {
    private final Network network = Network.newNetwork();

    private final PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .withDatabaseName("fintech")
            .withUsername("postgres")
            .withPassword("postgres")
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(PaymentEngineContainer.class)));

    private final PaymentEngineContainer paymentEngineContainer = new PaymentEngineContainer()
            .withNetwork(network)
            .dependsOn(postgreSQLContainer)
            .withNetworkAliases("test-app")
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(PaymentEngineContainer.class)));

    public void start() {
        postgreSQLContainer.setPortBindings(List.of("5432:5432"));
        paymentEngineContainer.start();
    }

    public int getGrpcPort() {
        return paymentEngineContainer.getGrpcPort();
    }

    public void stop() {
        paymentEngineContainer.close();
        postgreSQLContainer.close();
    }
}

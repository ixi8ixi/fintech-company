package com.academy.fintech.pe.blackbox.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.Future;

public class PaymentEngineContainer extends GenericContainer<PaymentEngineContainer> {
    private static final int GRPC_PORT = 9090;

    public PaymentEngineContainer() {
        super(image());
    }

    @Override
    protected void configure() {
        super.configure();
        withExposedPorts(GRPC_PORT);
        withStartupTimeout(Duration.ofSeconds(120));
    }

    private static Future<String> image() {
        Path dockerfilePath = Paths.get(System.getProperty("user.dir"), "Dockerfile");
        return new ImageFromDockerfile("test-app", true).withDockerfile(dockerfilePath);
    }

    public int getGrpcPort() {
        return this.getMappedPort(GRPC_PORT);
    }
}

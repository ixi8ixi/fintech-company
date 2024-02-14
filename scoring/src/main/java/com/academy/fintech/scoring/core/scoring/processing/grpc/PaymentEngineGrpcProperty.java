package com.academy.fintech.scoring.core.scoring.processing.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scoring.client.payment-engine.grpc")
public record PaymentEngineGrpcProperty(String host, int port) {}

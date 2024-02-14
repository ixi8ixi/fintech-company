package com.academy.fintech.scoring.configuration;

import com.academy.fintech.scoring.core.scoring.processing.grpc.PaymentEngineGrpcProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ PaymentEngineGrpcProperty.class })
public class ScoringConfiguration {}

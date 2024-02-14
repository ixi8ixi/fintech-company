package com.academy.fintech.origination.configuration;

import com.academy.fintech.origination.core.service.scoring.grpc.ScoringGrpcClientProperty;
import com.academy.fintech.origination.core.service.scoring.scheduled.ScheduledProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({ ScoringGrpcClientProperty.class, ScheduledProperty.class })
public class OriginationConfiguration {}

package com.academy.fintech.origination.core.service.scoring.scheduled;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scoring.scheduled")
public record ScheduledProperty(
   int batchSize
) {}

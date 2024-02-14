package com.academy.fintech.origination.core.service.scoring;

import com.academy.fintech.origination.core.service.application.ScoringApplicationService;
import com.academy.fintech.origination.core.service.scoring.grpc.ScoringGrpcClient;
import com.academy.fintech.origination.core.service.scoring.scheduled.ScheduledProperty;
import com.academy.fintech.origination.grpc.application.v1.dto.ApplicationScoringDto;
import com.academy.fintech.scoring.ScoreRequest;
import com.academy.fintech.scoring.ScoreResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Service for managing scheduled evaluations in the scoring service for new applications.
 */
@Service
@RequiredArgsConstructor
public class ScheduledScoringService {
    private final ConcurrentLinkedQueue<ApplicationScoringDto> taskQueue = new ConcurrentLinkedQueue<>();
    private final ScoringGrpcClient scoringGrpcClient;
    private final ScoringApplicationService scoringApplicationService;
    private final ScheduledProperty property;

    /**
     * Add an application to the queue for review in the scoring service.
     */
    public void submit(ApplicationScoringDto dto) {
        taskQueue.add(dto);
    }

    /**
     * Evaluate a new batch of applications and update their status accordingly in the database.
     */
    @Scheduled(fixedRateString = "${scoring.scheduled.rate}")
    public void scoreBatch() {
        List<ApplicationScoringDto> batch = pollBatch();

        for (ApplicationScoringDto dto : batch) {
            ScoreResponse response = scoringGrpcClient.score(ScoreRequest.newBuilder()
                    .setClientId(dto.clientId())
                    .setRequestedDisbursementAmount(dto.requestedDisbursementAmount().toString())
                    .setSalary(dto.salary().toString())
                    .build()
            );

            if (response.getApproved()) {
                scoringApplicationService.scoringAcceptApplication(dto.applicationId());
            } else {
                scoringApplicationService.scoringRejectApplication(dto.applicationId());
            }
        }
    }

    /**
     * Retrieve a new batch of applications for scoring service review. Pulls applications
     * up to the specified {@link ScheduledProperty#batchSize()} or until they are exhausted.
     */
    private List<ApplicationScoringDto> pollBatch() {
        List<ApplicationScoringDto> batch = new ArrayList<>();
        for (int i = 0; i < property.batchSize(); i++) {
            ApplicationScoringDto tail = taskQueue.poll();
            if (tail != null) {
                batch.add(tail);
            } else {
                break;
            }
        }
        return batch;
    }
}

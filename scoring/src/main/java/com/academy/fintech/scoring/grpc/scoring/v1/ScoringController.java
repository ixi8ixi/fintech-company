package com.academy.fintech.scoring.grpc.scoring.v1;

import com.academy.fintech.scoring.ScoreRequest;
import com.academy.fintech.scoring.ScoreResponse;
import com.academy.fintech.scoring.ScoringServiceGrpc;
import com.academy.fintech.scoring.core.scoring.processing.ScoringService;
import com.academy.fintech.scoring.dto.scoring.ScoringDto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;

/**
 * GRPC controller for scoring service, handles requests for scoring loan applications.
 */
@Slf4j
@GRpcService
@RequiredArgsConstructor
public class ScoringController extends ScoringServiceGrpc.ScoringServiceImplBase {
    private final ScoringService scoringService;

    /**
     * Request information about the user's loans and the estimated monthly payment from the payment engine.
     * Evaluate the application based on the rules described in the {@link ScoringService}, and if the application
     * scores at least one point, approve it; otherwise, reject it.
     */
    @Override
    public void score(ScoreRequest request, StreamObserver<ScoreResponse> responseObserver) {
        responseObserver.onNext(ScoreResponse.newBuilder()
                .setApproved(scoringService.score(requestToDto(request)))
                .build());
        responseObserver.onCompleted();
    }

    private static ScoringDto requestToDto(ScoreRequest request) {
        return new ScoringDto(
                new BigDecimal(request.getSalary()),
                new BigDecimal(request.getRequestedDisbursementAmount()),
                request.getClientId()
        );
    }
}

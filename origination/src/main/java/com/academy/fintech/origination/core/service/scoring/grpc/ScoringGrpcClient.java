package com.academy.fintech.origination.core.service.scoring.grpc;

import com.academy.fintech.scoring.ScoreRequest;
import com.academy.fintech.scoring.ScoreResponse;
import com.academy.fintech.scoring.ScoringServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * GRPC client for interacting with the scoring service.
 */
@Slf4j
@Component
public class ScoringGrpcClient {
    private final ScoringServiceGrpc.ScoringServiceBlockingStub stub;

    public ScoringGrpcClient(ScoringGrpcClientProperty property) {
        Channel channel = ManagedChannelBuilder.forAddress(property.host(), property.port()).usePlaintext().build();
        this.stub = ScoringServiceGrpc.newBlockingStub(channel);
    }

    public ScoreResponse score(ScoreRequest request) {
        try {
            return stub.score(request);
        } catch (StatusRuntimeException e) {
            log.error("Got error from Origination by request: {}", request, e);
            throw e;
        }
    }
}

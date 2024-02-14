package com.academy.fintech.scoring.core.scoring.processing.grpc;

import com.academy.fintech.scoring.InfoRequest;
import com.academy.fintech.scoring.InfoResponse;
import com.academy.fintech.scoring.ScoringInfoGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * GRPC client for interacting with the payment-engine service.
 */
@Slf4j
@Component
public class PaymentEngineGrpcClient {
    private final ScoringInfoGrpc.ScoringInfoBlockingStub stub;

    public PaymentEngineGrpcClient(PaymentEngineGrpcProperty property) {
        Channel channel = ManagedChannelBuilder.forAddress(property.host(), property.port()).usePlaintext().build();
        this.stub = ScoringInfoGrpc.newBlockingStub(channel);
    }

    public InfoResponse scoringResponse(InfoRequest request) {
        try {
            return stub.info(request);
        } catch (StatusRuntimeException e) {
            log.error("Got error from Origination by request: {}", request, e);
            throw e;
        }
    }
}

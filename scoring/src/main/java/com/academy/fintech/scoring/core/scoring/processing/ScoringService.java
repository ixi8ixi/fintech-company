package com.academy.fintech.scoring.core.scoring.processing;

import com.academy.fintech.scoring.InfoRequest;
import com.academy.fintech.scoring.InfoResponse;
import com.academy.fintech.scoring.core.scoring.processing.grpc.PaymentEngineGrpcClient;
import com.academy.fintech.scoring.core.scoring.processing.utils.ScoringUtils;
import com.academy.fintech.scoring.dto.scoring.ScoringDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Credit scoring service. Evaluation is based on the following rules:
 * <pre>
 * 1. If the approximate monthly payment is less than one-third of the salary - +1 point; otherwise, +0 points.
 * 2. For each loan - if there is no overdue payment, +1 point; if the overdue is less than 7 days, +0 points;
 *    if the overdue is more than 7 days, -1 point.</pre>
 * If there are no loans, the evaluation under the second point is not applied.
 * <p>
 * If the final total of points is greater than 0, the application is approved.
 */
@Service
@RequiredArgsConstructor
public class ScoringService {
    private static final int DEFAULT_LOAN_TERM = 12;
    private static final String DEFAULT_INTEREST_RATE = "10.0";
    private final PaymentEngineGrpcClient paymentEngineGrpcClient;

    /**
     * Retrieve the estimated monthly payment and the list of payment dates for the
     * user's loans from the payment engine. Evaluate the application based on the
     * rules described above and return the assessment verdict.
     */
    public boolean score(ScoringDto dto) {
        int result = 0;

        InfoResponse response = paymentEngineGrpcClient.scoringResponse(InfoRequest.newBuilder()
            .setClientId(dto.clientId())
            .setLoanTerm(DEFAULT_LOAN_TERM)
            .setInterestRate(DEFAULT_INTEREST_RATE)
            .setRequestedDisbursementAmount(dto.requestedDisbursement().toString()).build()
        );

        result += ScoringUtils.checkFixedPayment(dto, response) ? 1 : 0;
        result += ScoringUtils.checkPaymentDates(response);

        return result > 0;
    }
}

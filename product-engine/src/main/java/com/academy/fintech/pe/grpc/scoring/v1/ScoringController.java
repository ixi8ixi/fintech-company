package com.academy.fintech.pe.grpc.scoring.v1;

import com.academy.fintech.pe.core.service.agreement.ScoringService;
import com.academy.fintech.pe.grpc.scoring.v1.dto.ScoringDto;
import com.academy.fintech.pe.grpc.scoring.v1.dto.ScoringResponseDto;
import com.academy.fintech.scoring.InfoRequest;
import com.academy.fintech.scoring.InfoResponse;
import com.academy.fintech.scoring.ScoringInfoGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.GRpcService;

import java.math.BigDecimal;

@GRpcService
@RequiredArgsConstructor
public class ScoringController extends ScoringInfoGrpc.ScoringInfoImplBase {
    private final ScoringService scoringService;

    /**
     * Calculates the approximate monthly payment and returns it, along with all the dates
     * of the next payments for the specified client's loans, back to the origination service.
     * Returns INVALID_ARGUMENT if request contains null fields or numbers in invalid format.
     * Returns FAILED_PRECONDITION if request contains values out of legal range.
     */
    @Override
    public void info(InfoRequest request, StreamObserver<InfoResponse> responseObserver) {
        try {
            ScoringResponseDto responseDto = scoringService.score(mapToDto(request));
            responseObserver.onNext(mapToResponse(responseDto));
            responseObserver.onCompleted();
        } catch (NullPointerException | NumberFormatException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException());
        }
    }

    /**
     * Map request to dto with specified disbursement amount, loan term and interest rate.
     *
     * @throws NullPointerException if interest rate, disbursement amount or loan term in request are null
     * @throws NumberFormatException if request contains number in invalid format
     * @throws IllegalArgumentException if request contains negative numbers
     */
    private ScoringDto mapToDto(InfoRequest request) {
        BigDecimal requestedDisbursementAmount = new BigDecimal(request.getRequestedDisbursementAmount());
        BigDecimal interestRate = new BigDecimal(request.getInterestRate());
        int loanTerm = request.getLoanTerm();
        if (requestedDisbursementAmount.compareTo(BigDecimal.ZERO) < 0
                || interestRate.compareTo(BigDecimal.ZERO) < 0
                || loanTerm < 0) {
            throw new IllegalArgumentException("Disbursement amount, interest rate and loan term should be positive");
        }
        return new ScoringDto(request.getClientId(),
                requestedDisbursementAmount,
                interestRate,
                loanTerm);
    }

    private InfoResponse mapToResponse(ScoringResponseDto dto) {
        InfoResponse.Builder builder = InfoResponse.newBuilder();
        builder.setFixedPayment(dto.pmt().toString());
        dto.paymentDates().forEach(date -> builder.addPaymentDates(date.toString()));
        return builder.build();
    }
}

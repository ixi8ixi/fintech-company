package com.academy.fintech.pe.core.service.agreement.db.agreement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgreementServiceImpl implements AgreementService {
    private final AgreementRepository agreementRepository;

    @Override
    public long addAgreement(Agreement agreement) {
        return agreementRepository.save(agreement).getAgreementId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Optional<Agreement> activateAgreement(long agreementNumber, LocalDate disbursementDate, LocalDate nextPaymentDate) {
        Optional<Agreement> agreementCandidate = agreementRepository.findById(agreementNumber);
        if (agreementCandidate.isEmpty()) {
            return Optional.empty();
        }

        Agreement agreement = agreementCandidate.get();

        if (agreement.getStatus() != Agreement.AgreementStatus.PENDING) {
            return Optional.empty();
        }

        agreement.setStatus(Agreement.AgreementStatus.ACCEPTED);
        agreement.setDisbursementDate(disbursementDate);
        agreement.setNextPaymentDate(nextPaymentDate);

        return Optional.of(agreementRepository.save(agreement));
    }

    @Override
    public List<LocalDate> acceptedPaymentDates(long clientId) {
        return agreementRepository.paymentDatesByClientId(clientId, Agreement.AgreementStatus.ACCEPTED);
    }
}

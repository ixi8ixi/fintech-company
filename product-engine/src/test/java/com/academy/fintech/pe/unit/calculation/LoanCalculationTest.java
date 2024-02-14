package com.academy.fintech.pe.unit.calculation;

import com.academy.fintech.pe.core.service.agreement.db.agreement.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.payment.Payment;
import com.academy.fintech.pe.core.service.agreement.utils.LoanUtils;
import org.apache.poi.ss.formula.functions.Finance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LoanCalculationTest {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final BigDecimal ALLOWABLE_ERROR = new BigDecimal("0.5");
    private static final Agreement SAMPLE_AGREEMENT = Agreement.builder()
            .agreementId(1L)
            .disbursementAmount(new BigDecimal("400000"))
            .interest(new BigDecimal("13.3"))
            .loanTerm(15)
            .principalAmount(new BigDecimal("405000"))
            .clientId(123L)
            .status(Agreement.AgreementStatus.PENDING)
            .productCode("CL1.0.0")
            .build();
    private static final LocalDate SAMPLE_DATE = LocalDate.parse("2000-01-01");

    @Test
    public void one_pmt() {
        pmtCheckForAgreement(SAMPLE_AGREEMENT);
    }

    @Test
    public void random_pmt() {
        for (int i = 0; i < 100; i++) {
            pmtCheckForAgreement(randomAgreement());
        }
    }

    @Test
    public void one_ipmt() {
        ipmtCheckForAgreement(SAMPLE_AGREEMENT);
    }

    @Test
    public void random_ipmt() {
        for (int i = 0; i < 100; i++) {
            ipmtCheckForAgreement(randomAgreement());
        }
    }

    @Test
    public void one_ppmt() {
        ppmtCheckForAgreement(SAMPLE_AGREEMENT);
    }

    @Test
    public void random_ppmt() {
        for (int i = 0; i < 100; i++) {
            ppmtCheckForAgreement(randomAgreement());
        }
    }

    @Test
    public void one_payment_created_correctly() {
        paymentCreationCheckForAgreement(SAMPLE_AGREEMENT, LocalDate.parse("2000-12-31"), 12378426L);
    }

    @Test
    public void random_payments_created_correctly() {
        for (int i = 0; i < 100; i++) {
            paymentCreationCheckForAgreement(randomAgreement(), randomDate(),
                    RANDOM.nextLong(0, 1000000));
        }
    }

    private LocalDate randomDate() {
        return SAMPLE_DATE.plusMonths(RANDOM.nextInt(0, 100))
                .plusDays(RANDOM.nextInt(0, 366));
    }

    private boolean approximatelyEqual(double a, BigDecimal b) {
        BigDecimal result = b.setScale(1, RoundingMode.HALF_UP)
                .subtract(BigDecimal.valueOf(a).setScale(1, RoundingMode.HALF_UP)).abs();
        return result.compareTo(ALLOWABLE_ERROR) <= 0;
    }

    private void pmtCheckForAgreement(Agreement agreement) {
        BigDecimal monthlyInterestRate = LoanUtils.countMonthlyInterestRate(agreement.getInterest());
        BigDecimal actual = LoanUtils.fixedMonthlyPayment(agreement, monthlyInterestRate);
        double expected = Finance.pmt(monthlyInterestRate.doubleValue(),
                agreement.getLoanTerm(), agreement.getPrincipalAmount().doubleValue()) * -1;
        Assertions.assertTrue(approximatelyEqual(expected, actual));
    }

    private void ipmtCheckForAgreement(Agreement agreement) {
        BigDecimal monthlyInterestRate = LoanUtils.countMonthlyInterestRate(agreement.getInterest());
        BigDecimal pmt = LoanUtils.fixedMonthlyPayment(agreement, monthlyInterestRate);
        List<BigDecimal> ipmt = LoanUtils.interestPortions(agreement, monthlyInterestRate, pmt);
        for (int i = 0; i < agreement.getLoanTerm(); i++) {
            double expected = Finance.ipmt(monthlyInterestRate.doubleValue(), i + 1,
                    agreement.getLoanTerm(), agreement.getPrincipalAmount().doubleValue()) * -1;
            BigDecimal actual = ipmt.get(i);
            Assertions.assertTrue(approximatelyEqual(expected, actual));
        }
    }

    private void ppmtCheckForAgreement(Agreement agreement) {
        BigDecimal monthlyInterestRate = LoanUtils.countMonthlyInterestRate(agreement.getInterest());
        BigDecimal pmt = LoanUtils.fixedMonthlyPayment(agreement, monthlyInterestRate);
        List<BigDecimal> ipmt = LoanUtils.interestPortions(agreement, monthlyInterestRate, pmt);
        List<BigDecimal> ppmt = LoanUtils.principalPortions(ipmt, pmt);
        for (int i = 0; i < agreement.getLoanTerm(); i++) {
            double expected = Finance.ppmt(monthlyInterestRate.doubleValue(), i + 1,
                    agreement.getLoanTerm(), agreement.getPrincipalAmount().doubleValue()) * -1;
            BigDecimal actual = ppmt.get(i);
            Assertions.assertTrue(approximatelyEqual(expected, actual));
        }
    }

    private void paymentCreationCheckForAgreement(Agreement agreement, LocalDate disbursementDate, long scheduleId) {
        LocalDate firstPaymentDate = disbursementDate.plusMonths(1);
        List<Payment> paymentList = LoanUtils.createPayments(agreement, firstPaymentDate, scheduleId);
        double monthlyInterestRate = LoanUtils.countMonthlyInterestRate(agreement.getInterest()).doubleValue();
        double pmt = Finance.pmt(monthlyInterestRate, agreement.getLoanTerm(),
                agreement.getPrincipalAmount().doubleValue()) * -1;

        for (int i = 0; i < paymentList.size(); i++) {
            Payment payment = paymentList.get(i);
            double ipmt = Finance.ipmt(monthlyInterestRate, i + 1, agreement.getLoanTerm(),
                    agreement.getPrincipalAmount().doubleValue()) * -1;
            double ppmt = Finance.ppmt(monthlyInterestRate, i + 1, agreement.getLoanTerm(),
                    agreement.getPrincipalAmount().doubleValue()) * -1;

            Assertions.assertEquals(scheduleId, payment.getScheduleId());
            Assertions.assertEquals(Payment.PaymentStatus.PENDING, payment.getStatus());
            Assertions.assertEquals(firstPaymentDate.plusMonths(i), payment.getPaymentDate());
            Assertions.assertTrue(approximatelyEqual(pmt, payment.getPeriodPayment()));
            Assertions.assertTrue(approximatelyEqual(ipmt, payment.getInterestPayment()));
            Assertions.assertTrue(approximatelyEqual(ppmt, payment.getPrincipalPayment()));
            Assertions.assertEquals(i + 1, payment.getPeriodNumber());
        }
    }

    private Agreement randomAgreement() {
        long disbursementAmount = RANDOM.nextLong(50000, 400001);
        return Agreement.builder()
                .agreementId(1L)
                .disbursementAmount(BigDecimal.valueOf(disbursementAmount))
                .interest(BigDecimal.valueOf(RANDOM.nextDouble(8.0, 15.0)))
                .loanTerm(RANDOM.nextInt(3, 25))
                .principalAmount(BigDecimal.valueOf(disbursementAmount + 5000))
                .clientId(123L)
                .status(Agreement.AgreementStatus.PENDING)
                .productCode("CL1.0.0")
                .build();
    }
}

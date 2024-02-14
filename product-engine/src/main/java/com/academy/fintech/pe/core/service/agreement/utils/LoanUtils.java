package com.academy.fintech.pe.core.service.agreement.utils;

import com.academy.fintech.pe.core.service.agreement.db.agreement.Agreement;
import com.academy.fintech.pe.core.service.agreement.db.payment.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Methods for Calculating Loan Repayment Schedule.
 */
public class LoanUtils {
    private static final BigDecimal YEAR_INTEREST_DIVISOR = new BigDecimal("1200");
    private static final BigDecimal MINUS_ONE = new BigDecimal("-1");

    private LoanUtils() {
        // No instance
        throw new AssertionError();
    }

    /**
     * Calculation of the next payment date, taking into account a possible interest-free period.
     *
     * @param interestFreePeriod interest-free period in months
     * @return first payment date. In case there is no specific day in the month when payments
     * were made (e.g., 31st), the date is returned with the maximum possible number of months.
     * @throws DateTimeException if interestFreePeriod exceeds the supported date range of
     * LocalDate
     */
    public static LocalDate firstPaymentDate(LocalDate disbursementDate, int interestFreePeriod) {
        return disbursementDate.plusMonths(interestFreePeriod);
    }

    /**
     * The same as {@link LoanUtils#firstPaymentDate(LocalDate, int)} with interestFreePeriod = 1.
     */
    public static LocalDate firstPaymentDate(LocalDate disbursementDate) {
        return firstPaymentDate(disbursementDate, 1);
    }

    /**
     * Returns annual interest rate with scale 5.
     */
    public static BigDecimal countMonthlyInterestRate(BigDecimal annualInterestRate) {
        return annualInterestRate.divide(YEAR_INTEREST_DIVISOR, 5, RoundingMode.HALF_UP);
    }

    /**
     * Calculate the fixed monthly payment for the given agreement with the specified precalculated interest rate.
     */
    public static BigDecimal fixedMonthlyPayment(Agreement agreement, BigDecimal monthlyInterestRate) {
        return fixedMonthlyPayment(agreement.getLoanTerm(), agreement.getPrincipalAmount(), monthlyInterestRate);
    }

    /**
     * Calculate the fixed monthly payment for the given loan term and principal amount with the specified
     * precalculated interest rate.
     */
    public static BigDecimal fixedMonthlyPayment(int loanTerm, BigDecimal principalAmount, BigDecimal monthlyInterestRate) {
        BigDecimal factor = monthlyInterestRate.add(BigDecimal.ONE).pow(loanTerm);
        BigDecimal numerator = monthlyInterestRate.multiply(factor).multiply(principalAmount);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    /**
     * Returns a list of interest payments for the entire anticipated repayment period of the loan.
     * @param monthlyInterestRate precalculated monthly interest rate
     * @param pmt precalculated fixed monthly payment
     */
    public static List<BigDecimal> interestPortions(Agreement agreement, BigDecimal monthlyInterestRate, BigDecimal pmt) {
        BigDecimal base = monthlyInterestRate.add(BigDecimal.ONE);
        BigDecimal current = BigDecimal.ONE;

        ArrayList<BigDecimal> factors = new ArrayList<>();
        for (int i = 0; i < agreement.getLoanTerm(); i++) {
            factors.add(current);
            current = current.multiply(base);
        }

        BigDecimal negPmt = pmt.multiply(MINUS_ONE);
        return factors.stream().map(factor -> {
            BigDecimal first = negPmt.multiply(factor.subtract(BigDecimal.ONE));
            BigDecimal second = agreement.getPrincipalAmount().multiply(factor).multiply(monthlyInterestRate);
            return first.add(second).setScale(2, RoundingMode.HALF_UP);
        }).toList();
    }

    /**
     * Returns a list of principal payments for the entire anticipated repayment period of the loan.
     * @param interestPortions list of interest payments for the entire anticipated repayment period
     * @param pmt precalculated fixed monthly payment
     */
    public static List<BigDecimal> principalPortions(List<BigDecimal> interestPortions, BigDecimal pmt) {
        return interestPortions.stream().map(pmt::subtract).toList();
    }

    /**
     * Returns a list of payments for the specified period of the loan.
     *
     * @param scheduleId schedule id used to calculate payments
     */
    public static List<Payment> createPayments(Agreement agreement, LocalDate firstPayment, long scheduleId) {
        BigDecimal monthlyInterestRate = countMonthlyInterestRate(agreement.getInterest());
        BigDecimal pmt = fixedMonthlyPayment(agreement, monthlyInterestRate);
        List<BigDecimal> ipmt = interestPortions(agreement, monthlyInterestRate, pmt);
        List<BigDecimal> ppmt = principalPortions(ipmt, pmt);
        ArrayList<Payment> schedulePayments = new ArrayList<>();

        for (int i = 0; i < agreement.getLoanTerm(); i++) {
            schedulePayments.add(Payment.builder()
                    .scheduleId(scheduleId)
                    .status(Payment.PaymentStatus.PENDING)
                    .paymentDate(firstPayment.plusMonths(i))
                    .periodPayment(pmt)
                    .interestPayment(ipmt.get(i))
                    .principalPayment(ppmt.get(i))
                    .periodNumber(i + 1)
                    .build()
            );
        }
        return schedulePayments;
    }
}

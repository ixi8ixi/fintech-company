package com.academy.fintech.scoring.core.scoring.processing.utils;

import com.academy.fintech.scoring.InfoResponse;
import com.academy.fintech.scoring.dto.scoring.ScoringDto;
import com.academy.fintech.scoring.core.scoring.processing.ScoringService;
import com.google.protobuf.ProtocolStringList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ScoringUtils {
    private static final BigDecimal THREE = new BigDecimal("3");

    private ScoringUtils() {
        // no instance
        // exception in case of calling a constructor using reflection
        throw new AssertionError();
    }

    /**
     * Check fixed payment if it less than client's salary.
     */
    public static boolean checkFixedPayment(ScoringDto dto, InfoResponse response) {
        BigDecimal fixedPayment = new BigDecimal(response.getFixedPayment());
        BigDecimal thirdOfSalary = dto.salary().divide(THREE, RoundingMode.HALF_UP);
        return fixedPayment.compareTo(thirdOfSalary) < 0;
    }

    /**
     * Returns the credit assessment for the client in points. Overdue payments are taken
     * into account from the moment the method is initiated.
     *
     * @see ScoringService
     */
    public static int checkPaymentDates(InfoResponse response) {
        LocalDate now = LocalDate.now();
        int result = 0;
        ProtocolStringList list = response.getPaymentDatesList();
        if (!list.isEmpty()) {
            for (String strDate : list) {
                LocalDate date = LocalDate.parse(strDate);
                long difference = ChronoUnit.DAYS.between(date, now);
                if (difference <= 0) {
                    result += 1;
                } else if (difference > 7) {
                    result -= 1;
                }
            }
        }
        return result;
    }
}

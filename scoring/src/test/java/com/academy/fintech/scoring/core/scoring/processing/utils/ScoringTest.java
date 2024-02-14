package com.academy.fintech.scoring.core.scoring.processing.utils;

import com.academy.fintech.scoring.InfoResponse;
import com.academy.fintech.scoring.dto.scoring.ScoringDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ScoringTest {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final LocalDate START_DATE = LocalDate.now();

    @ParameterizedTest
    @CsvSource({"40000, 10000", "825476, 275155", "480000, 159000"})
    public void checkFixedPayment(String salary, String fixedPayment) {
        ScoringDto dto = new ScoringDto(new BigDecimal(salary), null, randomClient());
        InfoResponse response = InfoResponse.newBuilder().setFixedPayment(fixedPayment).build();
        Assertions.assertTrue(ScoringUtils.checkFixedPayment(dto, response));
    }

    @Test
    public void randomFixedPaymentTest() {
        for (int i = 0; i < 1000; i++) {
            int salary = randomSalary();
            ScoringDto dto = new ScoringDto(new BigDecimal(salary), null, randomClient());
            InfoResponse response = InfoResponse.newBuilder()
                    .setFixedPayment(Integer.toString(salary / 3 - 100))
                    .build();
            Assertions.assertTrue(ScoringUtils.checkFixedPayment(dto, response));
        }
    }

    @Test
    public void randomDatesScoring() {
        int expectedScoring;
        ArrayList<String> dates = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            expectedScoring = 0;
            dates.clear();

            for (int date = 0; date < 10; date++) {
                int delta = randomDaysRange();
                if (delta >= 0) {
                    expectedScoring += 1;
                } else if (delta < -7) {
                    expectedScoring -= 1;
                }
                dates.add(START_DATE.plusDays(delta).toString());
            }

            InfoResponse.Builder responseBuilder = InfoResponse.newBuilder();
            dates.forEach(responseBuilder::addPaymentDates);
            Assertions.assertEquals(expectedScoring,
                    ScoringUtils.checkPaymentDates(responseBuilder.build()));
        }
    }

    private static String randomClient() {
        return Long.toString(RANDOM.nextLong(0, Long.MAX_VALUE));
    }

    private static int randomSalary() {
        return RANDOM.nextInt(20000, 1000000);
    }

    private static int randomDaysRange() {
        return RANDOM.nextInt(-15, 15);
    }
}

package com.academy.fintech.origination.utils;

import com.academy.fintech.application.ApplicationRequest;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestUtils {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final AtomicInteger emailCounter = new AtomicInteger();

    private RequestUtils() {
        // no instance
        // An error for cases where someone attempts to invoke the constructor using Reflection.
        throw new AssertionError();
    }

    public static int randomMoney() {
        return RANDOM.nextInt(5000, 5000000);
    }

    public static String randomString() {
        int len = RANDOM.nextInt(7, 14);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append((char) RANDOM.nextInt('a', 'z' + 1));
        }
        return sb.toString();
    }

    public static String randomEmail() {
        // every generated email will be unique
        return emailCounter.incrementAndGet() +  randomString() + "@" + randomString() + ".com";
    }

    public static ApplicationRequest randomRequest() {
        return ApplicationRequest.newBuilder()
                .setDisbursementAmount(RequestUtils.randomMoney())
                .setSalary(RequestUtils.randomMoney())
                .setFirstName(RequestUtils.randomString())
                .setLastName(RequestUtils.randomString())
                .setEmail(RequestUtils.randomEmail())
                .build();
    }
}

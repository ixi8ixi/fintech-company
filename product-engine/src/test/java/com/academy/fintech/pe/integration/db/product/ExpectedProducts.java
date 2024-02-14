package com.academy.fintech.pe.integration.db.product;

import java.math.BigDecimal;

public enum ExpectedProducts {
    CASH_LOAN("CL1.0.0", ProductTestEntity.builder()
            .productCode("CL1.0.0")
            .loanTermMin(3)
            .loanTermMax(24)
            .principalAmountMin(new BigDecimal("50000"))
            .principalAmountMax(new BigDecimal("500000"))
            .interestMin(new BigDecimal("8"))
            .interestMax(new BigDecimal("15"))
            .originationAmountMin(new BigDecimal("2000"))
            .originationAmountMax(new BigDecimal("10000"))
            .build());

    private final String name;
    private final ProductTestEntity productEntity;

    ExpectedProducts(String name, ProductTestEntity entity) {
        this.name = name;
        this.productEntity = entity;
    }

    public ProductTestEntity entity() {
        return productEntity;
    }

    public String productName() {
        return name;
    }

    public static boolean inBounds(ExpectedProducts product, int loanTerm,
                                   BigDecimal principalAmount, BigDecimal interest, BigDecimal originationAmount) {
        ProductTestEntity entity = product.entity();
        return between(entity.getLoanTermMin(), loanTerm, entity.getLoanTermMax())
                && between(entity.getPrincipalAmountMin(), principalAmount, entity.getPrincipalAmountMax())
                && between(entity.getInterestMin(), interest, entity.getInterestMax())
                && between(entity.getOriginationAmountMin(), originationAmount, entity.getOriginationAmountMax());
    }

    private static <T extends Comparable<T>> boolean between(T lowerBound, T checkValue, T upperBound) {
        return checkValue.compareTo(lowerBound) >= 0
                && checkValue.compareTo(upperBound) <= 0;
    }
}

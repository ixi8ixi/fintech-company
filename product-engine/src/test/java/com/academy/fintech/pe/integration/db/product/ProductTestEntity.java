package com.academy.fintech.pe.integration.db.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Entity
@Table(name = "products")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ProductTestEntity {
    @Id
    private String productCode;
    private Integer loanTermMin;
    private Integer loanTermMax;
    private BigDecimal principalAmountMin;
    private BigDecimal principalAmountMax;
    private BigDecimal interestMin;
    private BigDecimal interestMax;
    private BigDecimal originationAmountMin;
    private BigDecimal originationAmountMax;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductTestEntity that = (ProductTestEntity) o;
        return Objects.equals(productCode, that.productCode)
                && Objects.equals(loanTermMin, that.loanTermMin)
                && Objects.equals(loanTermMax, that.loanTermMax)
                && principalAmountMin.compareTo(that.principalAmountMin) == 0
                && principalAmountMax.compareTo(that.principalAmountMax) == 0
                && interestMin.compareTo(that.interestMin) == 0
                && interestMax.compareTo(that.interestMax) == 0
                && originationAmountMin.compareTo(that.originationAmountMin) == 0
                && originationAmountMax.compareTo(that.originationAmountMax) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, loanTermMin, loanTermMax,
                principalAmountMin.doubleValue(), principalAmountMax.doubleValue(), interestMin.doubleValue(),
                interestMax.doubleValue(), originationAmountMin.doubleValue(), originationAmountMax.doubleValue());
    }
}

package com.academy.fintech.pe.core.service.agreement.db.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "products")
public class Product {
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
}

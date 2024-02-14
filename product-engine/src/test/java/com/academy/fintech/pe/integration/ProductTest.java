package com.academy.fintech.pe.integration;

import com.academy.fintech.pe.integration.config.DBSetupExtension;
import com.academy.fintech.pe.integration.config.DBTestConfiguration;
import com.academy.fintech.pe.integration.db.product.ExpectedProducts;
import com.academy.fintech.pe.integration.db.product.ProductTestEntity;
import com.academy.fintech.pe.integration.db.product.ProductTestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@ExtendWith(DBSetupExtension.class)
@Import(DBTestConfiguration.class)
public class ProductTest {
    @Autowired
    private ProductTestRepository productTestRepository;

    @Test
    public void set_of_products_is_fixed() {
        List<ProductTestEntity> allProducts = productTestRepository.findAll();
        Assertions.assertEquals(1, allProducts.size());
        ProductTestEntity firstProduct = allProducts.get(0);
        Assertions.assertEquals(ExpectedProducts.CASH_LOAN.entity(), firstProduct);
    }
}

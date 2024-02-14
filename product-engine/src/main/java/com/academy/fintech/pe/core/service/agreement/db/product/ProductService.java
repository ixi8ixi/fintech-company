package com.academy.fintech.pe.core.service.agreement.db.product;

import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(String productId);
}

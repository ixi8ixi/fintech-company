package com.academy.fintech.pe.core.service.agreement.db.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Optional<Product> findById(String productId) {
        return productRepository.findById(productId);
    }
}

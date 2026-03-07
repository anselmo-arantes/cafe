package com.anselmo.ecommerce.catalog.repository;

import com.anselmo.ecommerce.catalog.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);

    Optional<Product> findBySku(String sku);

    List<Product> findAll();
}

package com.anselmo.ecommerce.catalog.cart.gateway;

import com.anselmo.ecommerce.catalog.cart.dto.CatalogProductSnapshot;

import java.util.Optional;

public interface CatalogGateway {

    Optional<CatalogProductSnapshot> findBySku(String sku);
}

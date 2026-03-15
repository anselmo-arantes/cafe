package com.anselmo.ecommerce.cart.gateway;

import com.anselmo.ecommerce.cart.dto.CatalogProductSnapshot;

import java.util.Optional;

public interface CatalogGateway {

    Optional<CatalogProductSnapshot> findBySku(String sku);
}

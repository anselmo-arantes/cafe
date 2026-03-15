package com.anselmo.ecommerce.catalog.cart.dto;

import java.math.BigDecimal;

public record CatalogProductSnapshot(
        String sku,
        String name,
        BigDecimal price,
        String currency,
        boolean active
) {
}

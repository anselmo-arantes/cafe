package com.anselmo.ecommerce.catalog.dto;

import lombok.Builder;

@Builder
public record ProductAvailabilityResponse(
        String sku,
        boolean active,
        int stockQuantity,
        boolean available
) {
}

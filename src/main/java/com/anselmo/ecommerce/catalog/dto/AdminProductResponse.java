package com.anselmo.ecommerce.catalog.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record AdminProductResponse(
        String id,
        String sku,
        String name,
        String shortDescription,
        String fullDescription,
        BigDecimal price,
        String currency,
        boolean active,
        int stockQuantity,
        String mainImageUrl,
        List<String> imageUrls,
        Instant createdAt,
        Instant updatedAt,
        boolean available
) {
}

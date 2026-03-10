package com.anselmo.ecommerce.catalog.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record PublicProductResponse(
        String sku,
        String name,
        String shortDescription,
        String fullDescription,
        BigDecimal price,
        String currency,
        boolean available,
        String mainImageUrl,
        List<String> imageUrls
) {
}

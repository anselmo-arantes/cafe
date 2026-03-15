package com.anselmo.ecommerce.catalog.order.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        String sku,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}

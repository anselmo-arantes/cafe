package com.anselmo.ecommerce.cart.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemResponse(
        String sku,
        String name,
        Integer quantity,
        BigDecimal unitPrice,
        String currency,
        BigDecimal lineTotal
) {
}

package com.anselmo.ecommerce.catalog.cart.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record CartResponse(
        String cartId,
        List<CartItemResponse> items,
        BigDecimal subtotal,
        String currency,
        Instant updatedAt
) {
}

package com.anselmo.ecommerce.catalog.order.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderResponse(
        String orderId,
        String checkoutId,
        String customerEmail,
        BigDecimal totalAmount,
        String currency,
        String status,
        String paymentId,
        List<OrderItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {
}

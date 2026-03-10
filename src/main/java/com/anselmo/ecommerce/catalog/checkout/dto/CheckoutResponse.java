package com.anselmo.ecommerce.catalog.checkout.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CheckoutResponse(
        String checkoutId,
        String orderId,
        String paymentId,
        String status,
        BigDecimal amount,
        String currency,
        List<String> reservationIds
) {
}

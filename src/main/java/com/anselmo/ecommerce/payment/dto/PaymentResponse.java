package com.anselmo.ecommerce.payment.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record PaymentResponse(
        String paymentId,
        String orderId,
        BigDecimal amount,
        String currency,
        String status,
        String providerReference,
        List<String> reservationIds,
        Instant createdAt,
        Instant updatedAt
) {
}

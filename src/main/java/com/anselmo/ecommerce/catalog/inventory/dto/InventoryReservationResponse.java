package com.anselmo.ecommerce.catalog.inventory.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record InventoryReservationResponse(
        String reservationId,
        String sku,
        int quantity,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}

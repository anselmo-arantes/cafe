package com.anselmo.ecommerce.catalog.inventory.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record InventoryResponse(
        String sku,
        int availableQuantity,
        int reservedQuantity,
        int salableQuantity,
        Instant updatedAt
) {
}

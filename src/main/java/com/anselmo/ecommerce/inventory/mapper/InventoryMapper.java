package com.anselmo.ecommerce.inventory.mapper;

import com.anselmo.ecommerce.inventory.domain.InventoryItem;
import com.anselmo.ecommerce.inventory.domain.InventoryReservation;
import com.anselmo.ecommerce.inventory.dto.InventoryReservationResponse;
import com.anselmo.ecommerce.inventory.dto.InventoryResponse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(InventoryItem item) {
        return InventoryResponse.builder()
                .sku(item.getSku())
                .availableQuantity(item.getAvailableQuantity() == null ? 0 : item.getAvailableQuantity())
                .reservedQuantity(item.getReservedQuantity() == null ? 0 : item.getReservedQuantity())
                .salableQuantity(item.getSalableQuantity())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    public InventoryReservationResponse toResponse(InventoryReservation reservation) {
        return InventoryReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .sku(reservation.getSku())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}

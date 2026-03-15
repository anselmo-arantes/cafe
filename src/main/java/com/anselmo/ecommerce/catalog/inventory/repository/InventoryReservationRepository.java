package com.anselmo.ecommerce.catalog.inventory.repository;

import com.anselmo.ecommerce.catalog.inventory.domain.InventoryReservation;

import java.util.Optional;

public interface InventoryReservationRepository {

    Optional<InventoryReservation> findById(String reservationId);

    InventoryReservation save(InventoryReservation reservation);
}

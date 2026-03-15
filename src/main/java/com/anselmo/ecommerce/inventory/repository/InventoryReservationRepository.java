package com.anselmo.ecommerce.inventory.repository;

import com.anselmo.ecommerce.inventory.domain.InventoryReservation;

import java.util.Optional;

public interface InventoryReservationRepository {

    Optional<InventoryReservation> findById(String reservationId);

    InventoryReservation save(InventoryReservation reservation);
}

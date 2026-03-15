package com.anselmo.ecommerce.inventory.service;

import com.anselmo.ecommerce.inventory.domain.InventoryItem;
import com.anselmo.ecommerce.inventory.domain.InventoryReservation;
import com.anselmo.ecommerce.inventory.domain.ReservationStatus;
import com.anselmo.ecommerce.inventory.dto.AdjustInventoryRequest;
import com.anselmo.ecommerce.inventory.dto.CreateReservationRequest;
import com.anselmo.ecommerce.inventory.dto.InventoryReservationResponse;
import com.anselmo.ecommerce.inventory.dto.InventoryResponse;
import com.anselmo.ecommerce.inventory.exception.InsufficientInventoryException;
import com.anselmo.ecommerce.inventory.exception.InventoryNotFoundException;
import com.anselmo.ecommerce.inventory.exception.InventoryReservationNotFoundException;
import com.anselmo.ecommerce.inventory.gateway.InventoryCatalogGateway;
import com.anselmo.ecommerce.inventory.mapper.InventoryMapper;
import com.anselmo.ecommerce.inventory.repository.InventoryRepository;
import com.anselmo.ecommerce.inventory.repository.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final InventoryCatalogGateway catalogGateway;
    private final InventoryMapper inventoryMapper;

    public InventoryResponse getBySku(String sku) {
        return inventoryMapper.toResponse(findInventory(sku));
    }

    public InventoryResponse adjustInventory(String sku, AdjustInventoryRequest request) {
        validateSkuExists(sku);
        InventoryItem item = inventoryRepository.findBySku(sku).orElseGet(() -> newItem(sku));
        int reserved = safe(item.getReservedQuantity());
        if (request.getAvailableQuantity() < reserved) {
            throw new InsufficientInventoryException("Quantidade disponível não pode ser menor que reservada");
        }
        item.setAvailableQuantity(request.getAvailableQuantity());
        item.setUpdatedAt(Instant.now());
        inventoryRepository.save(item);
        return inventoryMapper.toResponse(item);
    }

    public InventoryReservationResponse reserve(CreateReservationRequest request) {
        validateSkuExists(request.getSku());
        var existing = reservationRepository.findById(request.getReservationId());
        if (existing.isPresent()) {
            return inventoryMapper.toResponse(existing.get());
        }

        InventoryItem item = inventoryRepository.findBySku(request.getSku()).orElseGet(() -> newItem(request.getSku()));
        if (item.getSalableQuantity() < request.getQuantity()) {
            throw new InsufficientInventoryException("Estoque insuficiente para reserva");
        }

        item.setReservedQuantity(safe(item.getReservedQuantity()) + request.getQuantity());
        item.setUpdatedAt(Instant.now());
        inventoryRepository.save(item);

        InventoryReservation reservation = new InventoryReservation();
        reservation.setReservationId(request.getReservationId());
        reservation.setSku(request.getSku());
        reservation.setQuantity(request.getQuantity());
        reservation.setStatus(ReservationStatus.RESERVED.name());
        reservation.setCreatedAt(Instant.now());
        reservation.setUpdatedAt(Instant.now());
        reservationRepository.save(reservation);

        return inventoryMapper.toResponse(reservation);
    }

    public InventoryReservationResponse confirmReservation(String reservationId) {
        InventoryReservation reservation = findReservation(reservationId);
        if (ReservationStatus.CONFIRMED.name().equals(reservation.getStatus())) {
            return inventoryMapper.toResponse(reservation);
        }

        if (ReservationStatus.RELEASED.name().equals(reservation.getStatus())) {
            throw new InsufficientInventoryException("Reserva já foi liberada");
        }

        InventoryItem item = findInventory(reservation.getSku());
        int quantity = reservation.getQuantity();
        int reserved = safe(item.getReservedQuantity());
        int available = safe(item.getAvailableQuantity());

        item.setReservedQuantity(Math.max(0, reserved - quantity));
        item.setAvailableQuantity(Math.max(0, available - quantity));
        item.setUpdatedAt(Instant.now());
        inventoryRepository.save(item);

        reservation.setStatus(ReservationStatus.CONFIRMED.name());
        reservation.setUpdatedAt(Instant.now());
        reservationRepository.save(reservation);

        return inventoryMapper.toResponse(reservation);
    }

    public InventoryReservationResponse releaseReservation(String reservationId) {
        InventoryReservation reservation = findReservation(reservationId);
        if (ReservationStatus.RELEASED.name().equals(reservation.getStatus())) {
            return inventoryMapper.toResponse(reservation);
        }

        if (ReservationStatus.CONFIRMED.name().equals(reservation.getStatus())) {
            throw new InsufficientInventoryException("Reserva já foi confirmada e não pode ser liberada");
        }

        InventoryItem item = findInventory(reservation.getSku());
        item.setReservedQuantity(Math.max(0, safe(item.getReservedQuantity()) - reservation.getQuantity()));
        item.setUpdatedAt(Instant.now());
        inventoryRepository.save(item);

        reservation.setStatus(ReservationStatus.RELEASED.name());
        reservation.setUpdatedAt(Instant.now());
        reservationRepository.save(reservation);

        return inventoryMapper.toResponse(reservation);
    }

    private InventoryItem findInventory(String sku) {
        return inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryNotFoundException("Estoque não encontrado"));
    }

    private InventoryReservation findReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InventoryReservationNotFoundException("Reserva não encontrada"));
    }

    private void validateSkuExists(String sku) {
        if (!catalogGateway.skuExists(sku)) {
            throw new InventoryNotFoundException("SKU não encontrado no catálogo");
        }
    }

    private InventoryItem newItem(String sku) {
        InventoryItem item = new InventoryItem();
        item.setSku(sku);
        item.setAvailableQuantity(0);
        item.setReservedQuantity(0);
        item.setUpdatedAt(Instant.now());
        return item;
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}

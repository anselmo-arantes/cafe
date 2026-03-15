package com.anselmo.ecommerce.inventory.controller;

import com.anselmo.ecommerce.inventory.dto.AdjustInventoryRequest;
import com.anselmo.ecommerce.inventory.dto.CreateReservationRequest;
import com.anselmo.ecommerce.inventory.dto.InventoryReservationResponse;
import com.anselmo.ecommerce.inventory.dto.InventoryResponse;
import com.anselmo.ecommerce.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/api/v1/inventory/{sku}")
    @Operation(summary = "Get inventory by SKU")
    public InventoryResponse getInventory(@PathVariable String sku) {
        return inventoryService.getBySku(sku);
    }

    @PatchMapping("/api/v1/admin/inventory/{sku}/adjust")
    @Operation(summary = "Adjust inventory available quantity")
    public InventoryResponse adjustInventory(@PathVariable String sku, @Valid @RequestBody AdjustInventoryRequest request) {
        return inventoryService.adjustInventory(sku, request);
    }

    @PostMapping("/api/v1/inventory/reservations")
    @Operation(summary = "Reserve inventory")
    public InventoryReservationResponse reserve(@Valid @RequestBody CreateReservationRequest request) {
        return inventoryService.reserve(request);
    }

    @PostMapping("/api/v1/inventory/reservations/{reservationId}/confirm")
    @Operation(summary = "Confirm inventory reservation")
    public InventoryReservationResponse confirm(@PathVariable String reservationId) {
        return inventoryService.confirmReservation(reservationId);
    }

    @PostMapping("/api/v1/inventory/reservations/{reservationId}/release")
    @Operation(summary = "Release inventory reservation")
    public InventoryReservationResponse release(@PathVariable String reservationId) {
        return inventoryService.releaseReservation(reservationId);
    }
}

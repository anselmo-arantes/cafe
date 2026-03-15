package com.anselmo.ecommerce.inventory.controller;

import com.anselmo.ecommerce.inventory.dto.AdjustInventoryRequest;
import com.anselmo.ecommerce.inventory.dto.CreateReservationRequest;
import com.anselmo.ecommerce.inventory.dto.InventoryReservationResponse;
import com.anselmo.ecommerce.inventory.dto.InventoryResponse;
import com.anselmo.ecommerce.inventory.exception.InventoryExceptionHandler;
import com.anselmo.ecommerce.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InventoryService inventoryService = new InventoryService(null, null, null, null) {
            @Override
            public InventoryResponse getBySku(String sku) {
                return InventoryResponse.builder()
                        .sku(sku)
                        .availableQuantity(10)
                        .reservedQuantity(2)
                        .salableQuantity(8)
                        .updatedAt(Instant.now())
                        .build();
            }

            @Override
            public InventoryResponse adjustInventory(String sku, AdjustInventoryRequest request) {
                return InventoryResponse.builder()
                        .sku(sku)
                        .availableQuantity(request.getAvailableQuantity())
                        .reservedQuantity(0)
                        .salableQuantity(request.getAvailableQuantity())
                        .updatedAt(Instant.now())
                        .build();
            }

            @Override
            public InventoryReservationResponse reserve(CreateReservationRequest request) {
                return InventoryReservationResponse.builder()
                        .reservationId(request.getReservationId())
                        .sku(request.getSku())
                        .quantity(request.getQuantity())
                        .status("RESERVED")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryController(inventoryService))
                .setControllerAdvice(new InventoryExceptionHandler())
                .build();
    }

    @Test
    void shouldGetInventory() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/CAFETEIRA-PORTATIL-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salableQuantity").value(8));
    }

    @Test
    void shouldAdjustInventory() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/inventory/CAFETEIRA-PORTATIL-001/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"availableQuantity\":25}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity").value(25));
    }

    @Test
    void shouldReserveInventory() throws Exception {
        mockMvc.perform(post("/api/v1/inventory/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservationId\":\"res-1\",\"sku\":\"CAFETEIRA-PORTATIL-001\",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVED"));
    }
}

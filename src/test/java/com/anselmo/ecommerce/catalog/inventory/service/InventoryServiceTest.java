package com.anselmo.ecommerce.catalog.inventory.service;

import com.anselmo.ecommerce.catalog.inventory.domain.InventoryItem;
import com.anselmo.ecommerce.catalog.inventory.domain.InventoryReservation;
import com.anselmo.ecommerce.catalog.inventory.domain.ReservationStatus;
import com.anselmo.ecommerce.catalog.inventory.dto.AdjustInventoryRequest;
import com.anselmo.ecommerce.catalog.inventory.dto.CreateReservationRequest;
import com.anselmo.ecommerce.catalog.inventory.exception.InsufficientInventoryException;
import com.anselmo.ecommerce.catalog.inventory.gateway.InventoryCatalogGateway;
import com.anselmo.ecommerce.catalog.inventory.mapper.InventoryMapper;
import com.anselmo.ecommerce.catalog.inventory.repository.InventoryRepository;
import com.anselmo.ecommerce.catalog.inventory.repository.InventoryReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryReservationRepository reservationRepository;

    @Mock
    private InventoryCatalogGateway catalogGateway;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(inventoryRepository, reservationRepository, catalogGateway, new InventoryMapper());
    }

    @Test
    void shouldAdjustInventory() {
        AdjustInventoryRequest request = new AdjustInventoryRequest();
        request.setAvailableQuantity(30);

        InventoryItem item = new InventoryItem();
        item.setSku("CAFETEIRA-PORTATIL-001");
        item.setAvailableQuantity(20);
        item.setReservedQuantity(2);

        when(catalogGateway.skuExists("CAFETEIRA-PORTATIL-001")).thenReturn(true);
        when(inventoryRepository.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = inventoryService.adjustInventory("CAFETEIRA-PORTATIL-001", request);

        assertThat(response.availableQuantity()).isEqualTo(30);
    }

    @Test
    void shouldReserveInventory() {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setReservationId("res-1");
        request.setSku("CAFETEIRA-PORTATIL-001");
        request.setQuantity(3);

        InventoryItem item = new InventoryItem();
        item.setSku("CAFETEIRA-PORTATIL-001");
        item.setAvailableQuantity(10);
        item.setReservedQuantity(1);

        when(catalogGateway.skuExists("CAFETEIRA-PORTATIL-001")).thenReturn(true);
        when(reservationRepository.findById("res-1")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = inventoryService.reserve(request);

        assertThat(response.status()).isEqualTo(ReservationStatus.RESERVED.name());
    }

    @Test
    void shouldFailReserveWhenInsufficient() {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setReservationId("res-2");
        request.setSku("CAFETEIRA-PORTATIL-001");
        request.setQuantity(20);

        InventoryItem item = new InventoryItem();
        item.setSku("CAFETEIRA-PORTATIL-001");
        item.setAvailableQuantity(5);
        item.setReservedQuantity(0);

        when(catalogGateway.skuExists("CAFETEIRA-PORTATIL-001")).thenReturn(true);
        when(reservationRepository.findById("res-2")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> inventoryService.reserve(request))
                .isInstanceOf(InsufficientInventoryException.class);
    }

    @Test
    void shouldConfirmReservation() {
        InventoryReservation reservation = new InventoryReservation();
        reservation.setReservationId("res-3");
        reservation.setSku("CAFETEIRA-PORTATIL-001");
        reservation.setQuantity(2);
        reservation.setStatus(ReservationStatus.RESERVED.name());

        InventoryItem item = new InventoryItem();
        item.setSku("CAFETEIRA-PORTATIL-001");
        item.setAvailableQuantity(10);
        item.setReservedQuantity(2);

        when(reservationRepository.findById("res-3")).thenReturn(Optional.of(reservation));
        when(inventoryRepository.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(reservationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = inventoryService.confirmReservation("res-3");

        assertThat(response.status()).isEqualTo(ReservationStatus.CONFIRMED.name());
    }


    @Test
    void shouldRejectConfirmWhenReservationAlreadyReleased() {
        InventoryReservation reservation = new InventoryReservation();
        reservation.setReservationId("res-rel");
        reservation.setSku("CAFETEIRA-PORTATIL-001");
        reservation.setQuantity(1);
        reservation.setStatus(ReservationStatus.RELEASED.name());

        when(reservationRepository.findById("res-rel")).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> inventoryService.confirmReservation("res-rel"))
                .isInstanceOf(InsufficientInventoryException.class);
    }

    @Test
    void shouldRejectReleaseWhenReservationAlreadyConfirmed() {
        InventoryReservation reservation = new InventoryReservation();
        reservation.setReservationId("res-conf");
        reservation.setSku("CAFETEIRA-PORTATIL-001");
        reservation.setQuantity(1);
        reservation.setStatus(ReservationStatus.CONFIRMED.name());

        when(reservationRepository.findById("res-conf")).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> inventoryService.releaseReservation("res-conf"))
                .isInstanceOf(InsufficientInventoryException.class);
    }

    @Test
    void shouldReturnExistingReservationWhenReservationIdIsReused() {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setReservationId("res-idempotent");
        request.setSku("CAFETEIRA-PORTATIL-001");
        request.setQuantity(1);

        InventoryReservation existing = new InventoryReservation();
        existing.setReservationId("res-idempotent");
        existing.setSku("CAFETEIRA-PORTATIL-001");
        existing.setQuantity(1);
        existing.setStatus(ReservationStatus.RESERVED.name());

        when(catalogGateway.skuExists("CAFETEIRA-PORTATIL-001")).thenReturn(true);
        when(reservationRepository.findById("res-idempotent")).thenReturn(Optional.of(existing));

        var response = inventoryService.reserve(request);

        assertThat(response.reservationId()).isEqualTo("res-idempotent");
        assertThat(response.status()).isEqualTo(ReservationStatus.RESERVED.name());
    }

}
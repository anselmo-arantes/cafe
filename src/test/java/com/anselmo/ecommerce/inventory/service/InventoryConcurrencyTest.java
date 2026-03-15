package com.anselmo.ecommerce.inventory.service;

import com.anselmo.ecommerce.inventory.domain.InventoryItem;
import com.anselmo.ecommerce.inventory.domain.InventoryReservation;
import com.anselmo.ecommerce.inventory.dto.CreateReservationRequest;
import com.anselmo.ecommerce.inventory.exception.InsufficientInventoryException;
import com.anselmo.ecommerce.inventory.gateway.InventoryCatalogGateway;
import com.anselmo.ecommerce.inventory.mapper.InventoryMapper;
import com.anselmo.ecommerce.inventory.repository.InventoryRepository;
import com.anselmo.ecommerce.inventory.repository.InventoryReservationRepository;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryConcurrencyTest {

    @Test
    void shouldAllowOnlyOneReservationForLastStockOnConcurrentRequests() throws InterruptedException {
        Map<String, InventoryItem> items = new ConcurrentHashMap<>();
        InventoryItem stock = new InventoryItem();
        stock.setSku("CAFETEIRA-PORTATIL-001");
        stock.setAvailableQuantity(1);
        stock.setReservedQuantity(0);
        items.put(stock.getSku(), stock);

        Map<String, InventoryReservation> reservations = new ConcurrentHashMap<>();

        InventoryRepository inventoryRepository = new InventoryRepository() {
            @Override
            public synchronized Optional<InventoryItem> findBySku(String sku) {
                return Optional.ofNullable(items.get(sku));
            }

            @Override
            public synchronized InventoryItem save(InventoryItem item) {
                items.put(item.getSku(), item);
                return item;
            }
        };

        InventoryReservationRepository reservationRepository = new InventoryReservationRepository() {
            @Override
            public synchronized Optional<InventoryReservation> findById(String reservationId) {
                return Optional.ofNullable(reservations.get(reservationId));
            }

            @Override
            public synchronized InventoryReservation save(InventoryReservation reservation) {
                reservations.put(reservation.getReservationId(), reservation);
                return reservation;
            }
        };

        InventoryCatalogGateway gateway = sku -> true;

        InventoryService inventoryService = new InventoryService(inventoryRepository, reservationRepository, gateway, new InventoryMapper());

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        Runnable reserveTask = () -> {
            try {
                start.await();
                CreateReservationRequest request = new CreateReservationRequest();
                request.setReservationId("res-" + Thread.currentThread().threadId());
                request.setSku("CAFETEIRA-PORTATIL-001");
                request.setQuantity(1);
                inventoryService.reserve(request);
                success.incrementAndGet();
            } catch (InsufficientInventoryException ex) {
                fail.incrementAndGet();
            } catch (Exception ignored) {
                fail.incrementAndGet();
            } finally {
                done.countDown();
            }
        };

        new Thread(reserveTask).start();
        new Thread(reserveTask).start();

        start.countDown();
        done.await();

        assertThat(success.get()).isEqualTo(1);
        assertThat(fail.get()).isEqualTo(1);
    }
}

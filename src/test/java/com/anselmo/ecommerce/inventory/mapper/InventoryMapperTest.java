package com.anselmo.ecommerce.inventory.mapper;

import com.anselmo.ecommerce.inventory.domain.InventoryItem;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMapperTest {

    private final InventoryMapper mapper = new InventoryMapper();

    @Test
    void shouldMapInventoryResponse() {
        InventoryItem item = new InventoryItem();
        item.setSku("CAFETEIRA-PORTATIL-001");
        item.setAvailableQuantity(10);
        item.setReservedQuantity(2);
        item.setUpdatedAt(Instant.now());

        var response = mapper.toResponse(item);

        assertThat(response.salableQuantity()).isEqualTo(8);
    }
}

package com.anselmo.ecommerce.catalog.cart.mapper;

import com.anselmo.ecommerce.catalog.cart.domain.CartItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartMapperTest {

    private final CartMapper mapper = new CartMapper();

    @Test
    void shouldMapCartWithSubtotal() {
        CartItem item = new CartItem();
        item.setCartId("cart-1");
        item.setSku("CAFETEIRA-PORTATIL-001");
        item.setProductName("Cafeteira Portátil");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("249.90"));
        item.setCurrency("BRL");
        item.setUpdatedAt(Instant.now());

        var response = mapper.toResponse("cart-1", List.of(item));

        assertThat(response.subtotal()).isEqualByComparingTo("499.80");
        assertThat(response.items()).hasSize(1);
    }
}

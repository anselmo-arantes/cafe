package com.anselmo.ecommerce.cart.service;

import com.anselmo.ecommerce.cart.domain.CartItem;
import com.anselmo.ecommerce.cart.dto.AddCartItemRequest;
import com.anselmo.ecommerce.cart.dto.CatalogProductSnapshot;
import com.anselmo.ecommerce.cart.dto.UpdateCartItemRequest;
import com.anselmo.ecommerce.cart.exception.CartItemNotFoundException;
import com.anselmo.ecommerce.cart.gateway.CatalogGateway;
import com.anselmo.ecommerce.cart.mapper.CartMapper;
import com.anselmo.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CatalogGateway catalogGateway;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, new CartMapper(), catalogGateway);
    }

    @Test
    void shouldAddItemAndReturnCart() {
        AddCartItemRequest request = new AddCartItemRequest();
        request.setSku("CAFETEIRA-PORTATIL-001");
        request.setQuantity(2);

        when(catalogGateway.findBySku("CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(
                new CatalogProductSnapshot("CAFETEIRA-PORTATIL-001", "Cafeteira", new BigDecimal("249.90"), "BRL", true)
        ));
        when(cartRepository.findItem("cart-1", "CAFETEIRA-PORTATIL-001")).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepository.findItemsByCartId("cart-1")).thenReturn(List.of(savedItem(2)));

        var response = cartService.addItem("cart-1", request);

        assertThat(response.subtotal()).isEqualByComparingTo("499.80");
    }

    @Test
    void shouldUpdateQuantity() {
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(3);

        when(cartRepository.findItem("cart-1", "CAFETEIRA-PORTATIL-001")).thenReturn(Optional.of(savedItem(1)));
        when(cartRepository.save(any(CartItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepository.findItemsByCartId("cart-1")).thenReturn(List.of(savedItem(3)));

        var response = cartService.updateItem("cart-1", "CAFETEIRA-PORTATIL-001", request);

        assertThat(response.items().getFirst().quantity()).isEqualTo(3);
    }

    @Test
    void shouldFailRemovingMissingItem() {
        when(cartRepository.findItem("cart-1", "MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.removeItem("cart-1", "MISSING"))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    private CartItem savedItem(int quantity) {
        CartItem item = new CartItem();
        item.setCartId("cart-1");
        item.setSku("CAFETEIRA-PORTATIL-001");
        item.setProductName("Cafeteira");
        item.setQuantity(quantity);
        item.setUnitPrice(new BigDecimal("249.90"));
        item.setCurrency("BRL");
        item.setUpdatedAt(java.time.Instant.now());
        return item;
    }
}

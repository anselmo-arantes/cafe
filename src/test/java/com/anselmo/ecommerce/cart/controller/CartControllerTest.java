package com.anselmo.ecommerce.cart.controller;

import com.anselmo.ecommerce.cart.dto.AddCartItemRequest;
import com.anselmo.ecommerce.cart.dto.CartResponse;
import com.anselmo.ecommerce.cart.exception.CartExceptionHandler;
import com.anselmo.ecommerce.cart.service.CartService;
import com.anselmo.ecommerce.catalog.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CartService cartService = new CartService(null, null, null) {
            @Override
            public CartResponse getCart(String cartId) {
                return CartResponse.builder()
                        .cartId(cartId)
                        .items(List.of())
                        .subtotal(BigDecimal.ZERO)
                        .currency("BRL")
                        .build();
            }

            @Override
            public CartResponse addItem(String cartId, AddCartItemRequest request) {
                return CartResponse.builder()
                        .cartId(cartId)
                        .items(List.of())
                        .subtotal(new BigDecimal("249.90"))
                        .currency("BRL")
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(cartService))
                .setControllerAdvice(new GlobalExceptionHandler(), new CartExceptionHandler())
                .build();
    }

    @Test
    void shouldGetCart() throws Exception {
        mockMvc.perform(get("/api/v1/cart/cart-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value("cart-1"));
    }

    @Test
    void shouldAddItem() throws Exception {
        mockMvc.perform(post("/api/v1/cart/cart-1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"CAFETEIRA-PORTATIL-001\",\"quantity\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(249.90));
    }
}

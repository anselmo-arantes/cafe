package com.anselmo.ecommerce.checkout.controller;

import com.anselmo.ecommerce.checkout.dto.CheckoutRequest;
import com.anselmo.ecommerce.checkout.dto.CheckoutResponse;
import com.anselmo.ecommerce.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CheckoutControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CheckoutService checkoutService = new CheckoutService(null, null, null, null) {
            @Override
            public CheckoutResponse checkout(CheckoutRequest request) {
                return CheckoutResponse.builder().checkoutId("c1").orderId("o1").paymentId("p1").status("PENDING_PAYMENT").amount(new BigDecimal("249.90")).currency("BRL").reservationIds(List.of("r1")).build();
            }
        };
        mockMvc = MockMvcBuilders.standaloneSetup(new CheckoutController(checkoutService)).build();
    }

    @Test
    void shouldCheckout() throws Exception {
        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartId\":\"cart-1\",\"customerEmail\":\"cliente@exemplo.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("o1"));
    }
}

package com.anselmo.ecommerce.payment.controller;

import com.anselmo.ecommerce.catalog.exception.GlobalExceptionHandler;
import com.anselmo.ecommerce.payment.dto.CreatePaymentRequest;
import com.anselmo.ecommerce.payment.dto.PaymentResponse;
import com.anselmo.ecommerce.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PaymentService paymentService = new PaymentService(null, null, null, null) {
            @Override
            public PaymentResponse createPayment(CreatePaymentRequest request) {
                return PaymentResponse.builder()
                        .paymentId("pay-1")
                        .orderId(request.getOrderId())
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
                        .status("PENDING")
                        .providerReference("mock-1")
                        .reservationIds(request.getReservationIds())
                        .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                        .updatedAt(Instant.parse("2026-01-01T00:00:00Z"))
                        .build();
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController(paymentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreatePayment() throws Exception {
        String requestBody = """
                {
                  "orderId": "order-1",
                  "amount": 249.90,
                  "currency": "BRL",
                  "reservationIds": ["res-1"]
                }
                """;

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("pay-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldReturnBadRequestWhenAmountIsNegative() throws Exception {
        String requestBody = """
                {
                  "orderId": "order-1",
                  "amount": -10,
                  "currency": "BRL",
                  "reservationIds": ["res-1"]
                }
                """;

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("amount")));
    }

    @Test
    void shouldReturnBadRequestWhenCurrencyIsNotIsoCode() throws Exception {
        String requestBody = """
                {
                  "orderId": "order-1",
                  "amount": 10,
                  "currency": "BR",
                  "reservationIds": ["res-1"]
                }
                """;

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message", containsString("currency")));
    }
}

package com.anselmo.ecommerce.catalog.payment.controller;

import com.anselmo.ecommerce.catalog.payment.dto.CreatePaymentRequest;
import com.anselmo.ecommerce.catalog.payment.dto.PaymentResponse;
import com.anselmo.ecommerce.catalog.payment.dto.PaymentWebhookRequest;
import com.anselmo.ecommerce.catalog.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/api/v1/payments")
    @Operation(summary = "Create payment")
    public PaymentResponse create(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/api/v1/payments/{paymentId}")
    @Operation(summary = "Get payment by id")
    public PaymentResponse getById(@PathVariable String paymentId) {
        return paymentService.getById(paymentId);
    }

    @PostMapping("/api/v1/payments/webhook")
    @Operation(summary = "Process payment webhook")
    public PaymentResponse webhook(@Valid @RequestBody PaymentWebhookRequest request) {
        return paymentService.processWebhook(request);
    }
}

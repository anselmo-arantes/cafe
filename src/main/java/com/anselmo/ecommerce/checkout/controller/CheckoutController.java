package com.anselmo.ecommerce.checkout.controller;

import com.anselmo.ecommerce.checkout.dto.CheckoutRequest;
import com.anselmo.ecommerce.checkout.dto.CheckoutResponse;
import com.anselmo.ecommerce.checkout.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    @Operation(summary = "Process checkout")
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }
}

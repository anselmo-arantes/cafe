package com.anselmo.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentWebhookRequest {

    @NotBlank
    private String paymentId;

    @NotBlank
    private String status;
}

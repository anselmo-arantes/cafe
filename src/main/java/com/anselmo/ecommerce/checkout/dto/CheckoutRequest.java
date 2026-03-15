package com.anselmo.ecommerce.checkout.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotBlank
    private String cartId;

    @NotBlank
    @Email
    private String customerEmail;
}

package com.anselmo.ecommerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemRequest {

    @NotBlank
    private String sku;

    @NotNull
    @Min(1)
    private Integer quantity;
}

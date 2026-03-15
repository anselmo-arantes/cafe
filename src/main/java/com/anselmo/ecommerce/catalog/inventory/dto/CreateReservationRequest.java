package com.anselmo.ecommerce.catalog.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReservationRequest {

    @NotBlank
    private String reservationId;

    @NotBlank
    private String sku;

    @NotNull
    @Min(1)
    private Integer quantity;
}

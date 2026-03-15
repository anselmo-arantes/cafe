package com.anselmo.ecommerce.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdjustInventoryRequest {

    @NotNull
    @Min(0)
    private Integer availableQuantity;
}

package com.anselmo.ecommerce.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePriceRequest {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;
}

package com.anselmo.ecommerce.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    private String shortDescription;
    private String fullDescription;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    private String currency;

    @NotNull
    private Boolean active;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    private String mainImageUrl;
    private List<String> imageUrls;
}

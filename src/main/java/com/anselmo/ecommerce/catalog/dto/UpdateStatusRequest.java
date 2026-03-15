package com.anselmo.ecommerce.catalog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull
    private Boolean active;
}

package com.lapalma.management.catalog.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String name,
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
        value = "0.0",
        inclusive = false,
        message = "El precio debe ser mayor que cero"
    )
    BigDecimal price
 ) {}

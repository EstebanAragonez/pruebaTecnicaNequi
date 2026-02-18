package com.nequi.franquicias.entrypoint.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotNull(message = "El ID de sucursal es requerido")
        @Positive(message = "El ID de sucursal debe ser positivo")
        Long branchId,
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String nombre,
        @NotNull(message = "El stock es requerido")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock) {}

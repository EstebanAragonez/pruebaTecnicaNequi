package com.nequi.franquicias.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BranchRequest(
        @NotNull(message = "El ID de franquicia es requerido")
        @Positive(message = "El ID de franquicia debe ser positivo")
        Long franchiseId,
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String nombre) {}

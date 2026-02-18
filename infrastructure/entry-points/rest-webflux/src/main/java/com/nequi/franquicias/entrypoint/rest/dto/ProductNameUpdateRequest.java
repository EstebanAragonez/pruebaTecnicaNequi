package com.nequi.franquicias.entrypoint.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductNameUpdateRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
        String nombre) {}

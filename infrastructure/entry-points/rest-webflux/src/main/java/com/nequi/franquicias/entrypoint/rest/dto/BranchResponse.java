package com.nequi.franquicias.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de sucursal")
public record BranchResponse(
        @Schema(description = "ID de la sucursal") Long id,
        @Schema(description = "Nombre de la sucursal") String nombre,
        @Schema(description = "ID de la franquicia padre") Long franchiseId) {}

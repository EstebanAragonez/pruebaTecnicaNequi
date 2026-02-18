package com.nequi.franquicias.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de producto")
public record ProductResponse(
        @Schema(description = "ID del producto") Long id,
        @Schema(description = "Nombre del producto") String nombre,
        @Schema(description = "Stock disponible") Integer stock,
        @Schema(description = "ID de la sucursal") Long branchId) {}

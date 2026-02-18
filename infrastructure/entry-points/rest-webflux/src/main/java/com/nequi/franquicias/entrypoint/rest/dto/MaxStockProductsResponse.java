package com.nequi.franquicias.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Respuesta de productos con mayor stock por sucursal")
public record MaxStockProductsResponse(
        @Schema(description = "ID de la franquicia") Long franchiseId,
        @Schema(description = "Lista de productos con mayor stock por sucursal") List<ProductWithBranchResponse> productos) {}

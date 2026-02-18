package com.nequi.franquicias.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto con información de sucursal")
public record ProductWithBranchResponse(
        @Schema(description = "ID del producto") Long productId,
        @Schema(description = "Nombre del producto") String productName,
        @Schema(description = "Stock") Integer stock,
        @Schema(description = "ID de la sucursal") Long branchId,
        @Schema(description = "Nombre de la sucursal") String branchName) {}

package com.nequi.franquicias.entrypoint.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de franquicia")
public record FranchiseResponse(
        @Schema(description = "ID de la franquicia") Long id,
        @Schema(description = "Nombre de la franquicia") String nombre) {}

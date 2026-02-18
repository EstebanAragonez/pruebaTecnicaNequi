package com.nequi.franquicias.model;

import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio que representa una franquicia.
 * Contiene sucursales con sus productos.
 */
public record Franchise(Long id, String nombre, List<Branch> sucursales) {

    public Franchise {
        Objects.requireNonNull(nombre, "El nombre de la franquicia no puede ser nulo");
        sucursales = sucursales != null ? List.copyOf(sucursales) : List.of();
    }
}

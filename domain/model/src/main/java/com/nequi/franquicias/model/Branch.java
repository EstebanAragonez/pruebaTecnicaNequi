package com.nequi.franquicias.model;

import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio que representa una sucursal.
 * Contiene productos ofertados.
 */
public record Branch(Long id, String nombre, Long franchiseId, List<Product> productos) {

    public Branch {
        Objects.requireNonNull(nombre, "El nombre de la sucursal no puede ser nulo");
        Objects.requireNonNull(franchiseId, "El franchiseId no puede ser nulo");
        productos = productos != null ? List.copyOf(productos) : List.of();
    }

    public Branch withProductos(List<Product> nuevosProductos) {
        return new Branch(id, nombre, franchiseId, nuevosProductos);
    }
}

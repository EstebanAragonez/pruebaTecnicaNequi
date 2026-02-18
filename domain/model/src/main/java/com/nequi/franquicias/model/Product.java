package com.nequi.franquicias.model;

import java.util.Objects;

/**
 * Entidad de dominio que representa un producto.
 * Inmutable - cumple principios de programación funcional.
 */
public record Product(Long id, String nombre, Integer stock, Long branchId) {

    public Product {
        Objects.requireNonNull(nombre, "El nombre del producto no puede ser nulo");
        Objects.requireNonNull(stock, "El stock no puede ser nulo");
        Objects.requireNonNull(branchId, "El branchId no puede ser nulo");
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    public Product withStock(Integer newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        return new Product(id, nombre, newStock, branchId);
    }
}

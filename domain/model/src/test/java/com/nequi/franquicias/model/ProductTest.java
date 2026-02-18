package com.nequi.franquicias.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product - Modelo de dominio")
class ProductTest {

    @Test
    @DisplayName("crea producto con valores válidos")
    void creaProductoValido() {
        Product product = new Product(1L, "Producto Test", 10, 5L);

        assertEquals(1L, product.id());
        assertEquals("Producto Test", product.nombre());
        assertEquals(10, product.stock());
        assertEquals(5L, product.branchId());
    }

    @Test
    @DisplayName("permite stock cero")
    void permiteStockCero() {
        Product product = new Product(1L, "Producto", 0, 1L);

        assertEquals(0, product.stock());
    }

    @Test
    @DisplayName("rechaza stock negativo")
    void rechazaStockNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(1L, "Producto", -1, 1L));
    }

    @Test
    @DisplayName("rechaza nombre nulo")
    void rechazaNombreNulo() {
        assertThrows(NullPointerException.class, () ->
                new Product(1L, null, 5, 1L));
    }

    @Test
    @DisplayName("rechaza stock nulo")
    void rechazaStockNulo() {
        assertThrows(NullPointerException.class, () ->
                new Product(1L, "Producto", null, 1L));
    }

    @Test
    @DisplayName("rechaza branchId nulo")
    void rechazaBranchIdNulo() {
        assertThrows(NullPointerException.class, () ->
                new Product(1L, "Producto", 5, null));
    }

    @Test
    @DisplayName("withStock actualiza stock correctamente")
    void withStockActualizaCorrectamente() {
        Product product = new Product(1L, "Producto", 10, 5L);
        Product actualizado = product.withStock(20);

        assertEquals(20, actualizado.stock());
        assertEquals(product.id(), actualizado.id());
        assertEquals(product.nombre(), actualizado.nombre());
    }

    @Test
    @DisplayName("withStock rechaza stock negativo")
    void withStockRechazaNegativo() {
        Product product = new Product(1L, "Producto", 10, 5L);

        assertThrows(IllegalArgumentException.class, () -> product.withStock(-1));
    }
}

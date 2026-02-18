package com.nequi.franquicias.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Branch - Modelo de dominio")
class BranchTest {

    @Test
    @DisplayName("crea sucursal con valores válidos")
    void creaSucursalValida() {
        Branch branch = new Branch(1L, "Sucursal Centro", 10L, List.of());

        assertEquals(1L, branch.id());
        assertEquals("Sucursal Centro", branch.nombre());
        assertEquals(10L, branch.franchiseId());
        assertTrue(branch.productos().isEmpty());
    }

    @Test
    @DisplayName("rechaza nombre nulo")
    void rechazaNombreNulo() {
        assertThrows(NullPointerException.class, () ->
                new Branch(1L, null, 10L, List.of()));
    }

    @Test
    @DisplayName("rechaza franchiseId nulo")
    void rechazaFranchiseIdNulo() {
        assertThrows(NullPointerException.class, () ->
                new Branch(1L, "Sucursal", null, List.of()));
    }

    @Test
    @DisplayName("productos nulos se convierten en lista vacía")
    void productosNullSeConvierteEnListaVacia() {
        Branch branch = new Branch(1L, "Test", 1L, null);

        assertNotNull(branch.productos());
        assertTrue(branch.productos().isEmpty());
    }

    @Test
    @DisplayName("withProductos crea nueva sucursal con productos")
    void withProductosCreaNuevaSucursal() {
        Branch branch = new Branch(1L, "Sucursal", 10L, List.of());
        Product product = new Product(1L, "Producto", 5, 1L);
        Branch conProductos = branch.withProductos(List.of(product));

        assertEquals(branch.id(), conProductos.id());
        assertEquals(1, conProductos.productos().size());
        assertEquals("Producto", conProductos.productos().getFirst().nombre());
    }

    @Test
    @DisplayName("productos son inmutables")
    void productosSonInmutables() {
        Branch branch = new Branch(1L, "Test", 1L, List.of());

        assertThrows(UnsupportedOperationException.class, () ->
                branch.productos().add(new Product(1L, "P", 1, 1L)));
    }
}

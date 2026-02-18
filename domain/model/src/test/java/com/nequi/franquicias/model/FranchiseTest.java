package com.nequi.franquicias.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Franchise - Modelo de dominio")
class FranchiseTest {

    @Test
    @DisplayName("crea franquicia con valores válidos")
    void creaFranquiciaValida() {
        Franchise franchise = new Franchise(1L, "Franquicia Test", List.of());

        assertEquals(1L, franchise.id());
        assertEquals("Franquicia Test", franchise.nombre());
        assertTrue(franchise.sucursales().isEmpty());
    }

    @Test
    @DisplayName("crea franquicia con sucursales")
    void creaFranquiciaConSucursales() {
        Branch branch = new Branch(1L, "Sucursal 1", 1L, List.of());
        Franchise franchise = new Franchise(1L, "Franquicia", List.of(branch));

        assertEquals(1, franchise.sucursales().size());
        assertEquals("Sucursal 1", franchise.sucursales().getFirst().nombre());
    }

    @Test
    @DisplayName("rechaza nombre nulo")
    void rechazaNombreNulo() {
        assertThrows(NullPointerException.class, () ->
                new Franchise(1L, null, List.of()));
    }

    @Test
    @DisplayName("sucursales nulas se convierten en lista vacía")
    void sucursalesNullSeConvierteEnListaVacia() {
        Franchise franchise = new Franchise(1L, "Test", null);

        assertNotNull(franchise.sucursales());
        assertTrue(franchise.sucursales().isEmpty());
    }

    @Test
    @DisplayName("sucursales son inmutables")
    void sucursalesSonInmutables() {
        Franchise franchise = new Franchise(1L, "Test", List.of());

        assertThrows(UnsupportedOperationException.class, () ->
                franchise.sucursales().add(new Branch(2L, "X", 1L, List.of())));
    }
}

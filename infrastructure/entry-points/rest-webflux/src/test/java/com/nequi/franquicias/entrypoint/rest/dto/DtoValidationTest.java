package com.nequi.franquicias.entrypoint.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO Validation")
class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("FranchiseRequest válido")
    void franchiseRequestValido() {
        var request = new FranchiseRequest("Franquicia Test");
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("FranchiseRequest rechaza nombre nulo")
    void franchiseRequestRechazaNombreNulo() {
        var request = new FranchiseRequest(null);
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("requerido")));
    }

    @Test
    @DisplayName("FranchiseRequest rechaza nombre en blanco")
    void franchiseRequestRechazaNombreBlanco() {
        var request = new FranchiseRequest("   ");
        Set<ConstraintViolation<FranchiseRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("BranchRequest válido")
    void branchRequestValido() {
        var request = new BranchRequest(1L, "Sucursal");
        Set<ConstraintViolation<BranchRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("BranchRequest rechaza franchiseId nulo")
    void branchRequestRechazaFranchiseIdNulo() {
        var request = new BranchRequest(null, "Sucursal");
        Set<ConstraintViolation<BranchRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("ProductRequest válido")
    void productRequestValido() {
        var request = new ProductRequest(1L, "Producto", 10);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("ProductRequest rechaza stock negativo")
    void productRequestRechazaStockNegativo() {
        var request = new ProductRequest(1L, "Producto", -1);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("negativo")));
    }

    @Test
    @DisplayName("StockUpdateRequest válido")
    void stockUpdateRequestValido() {
        var request = new StockUpdateRequest(25);
        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("StockUpdateRequest rechaza stock nulo")
    void stockUpdateRequestRechazaStockNulo() {
        var request = new StockUpdateRequest(null);
        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("FranchiseNameUpdateRequest válido")
    void franchiseNameUpdateRequestValido() {
        var request = new FranchiseNameUpdateRequest("Nuevo Nombre");
        Set<ConstraintViolation<FranchiseNameUpdateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("BranchNameUpdateRequest válido")
    void branchNameUpdateRequestValido() {
        var request = new BranchNameUpdateRequest("Nuevo Nombre");
        Set<ConstraintViolation<BranchNameUpdateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("ProductNameUpdateRequest válido")
    void productNameUpdateRequestValido() {
        var request = new ProductNameUpdateRequest("Nuevo Nombre");
        Set<ConstraintViolation<ProductNameUpdateRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}

package com.nequi.franquicias.entrypoint.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("maneja ResponseStatusException NOT_FOUND")
    void manejaResponseStatusNotFound() {
        var ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Franquicia no encontrada");

        ResponseEntity<ProblemDetail> response = handler.handleResponseStatus(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No encontrado", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("Franquicia no encontrada"));
    }

    @Test
    @DisplayName("maneja ResponseStatusException con razón nula")
    void manejaResponseStatusConRazonNula() {
        var ex = new ResponseStatusException(HttpStatus.NOT_FOUND);

        ResponseEntity<ProblemDetail> response = handler.handleResponseStatus(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("No encontrado", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("Recurso no encontrado"));
    }

    @Test
    @DisplayName("maneja IllegalArgumentException")
    void manejaIllegalArgumentException() {
        var ex = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<ProblemDetail> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Solicitud inválida", response.getBody().getTitle());
        assertEquals("Argumento inválido", response.getBody().getDetail());
    }

    @Test
    @DisplayName("maneja Exception genérica")
    void manejaExceptionGenerica() {
        var ex = new RuntimeException("Error inesperado");

        ResponseEntity<ProblemDetail> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error interno", response.getBody().getTitle());
    }

    @Test
    @DisplayName("maneja WebExchangeBindException")
    void manejaWebExchangeBindException() {
        var bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError("nombre", "nombre", "El nombre es requerido")));
        var ex = new WebExchangeBindException(null, bindingResult);

        ResponseEntity<ProblemDetail> response = handler.handleBindingValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error de validación", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("nombre"));
        assertTrue(response.getBody().getDetail().contains("El nombre es requerido"));
    }

    @Test
    @DisplayName("maneja ConstraintViolationException")
    void manejaConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("franchiseId");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("debe ser positivo");
        var ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ProblemDetail> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error de validación", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("franchiseId"));
        assertTrue(response.getBody().getDetail().contains("debe ser positivo"));
    }

    @Test
    @DisplayName("maneja HandlerMethodValidationException")
    void manejaHandlerMethodValidationException() {
        var methodParam = mock(org.springframework.core.MethodParameter.class);
        when(methodParam.getParameterName()).thenReturn("franchiseId");
        var error = new org.springframework.validation.ObjectError("franchiseId", "El ID debe ser positivo");
        var result = mock(org.springframework.validation.method.ParameterValidationResult.class);
        when(result.getMethodParameter()).thenReturn(methodParam);
        when(result.getResolvableErrors()).thenReturn(List.of(error));

        var ex = mock(HandlerMethodValidationException.class);
        when(ex.getAllValidationResults()).thenReturn(List.of(result));

        ResponseEntity<ProblemDetail> response = handler.handleMethodValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error de validación", response.getBody().getTitle());
        assertTrue(response.getBody().getDetail().contains("franchiseId"));
    }
}

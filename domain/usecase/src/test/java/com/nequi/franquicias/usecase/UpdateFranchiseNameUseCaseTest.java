package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Franchise;
import com.nequi.franquicias.model.port.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateFranchiseNameUseCase")
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private UpdateFranchiseNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateFranchiseNameUseCase(franchiseRepository);
    }

    @Test
    @DisplayName("actualiza nombre exitosamente")
    void actualizaNombreExitosamente() {
        Franchise existing = new Franchise(1L, "Viejo", java.util.List.of());
        Franchise updated = new Franchise(1L, "Nuevo", java.util.List.of());
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.execute(1L, "Nuevo"))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza franquicia no encontrada")
    void rechazaFranquiciaNoEncontrada() {
        when(franchiseRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(999L, "Nuevo"))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza nombre vacío")
    void rechazaNombreVacio() {
        Franchise existing = new Franchise(1L, "Viejo", java.util.List.of());
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.execute(1L, "  "))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza franchiseId nulo")
    void rechazaFranchiseIdNulo() {
        StepVerifier.create(useCase.execute(null, "Nuevo"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza nombre nulo")
    void rechazaNombreNulo() {
        Franchise existing = new Franchise(1L, "Viejo", java.util.List.of());
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.execute(1L, null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("trim del nombre antes de guardar")
    void trimNombreAntesDeGuardar() {
        Franchise existing = new Franchise(1L, "Viejo", java.util.List.of());
        Franchise updated = new Franchise(1L, "Nuevo", java.util.List.of());
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.execute(1L, "  Nuevo  "))
                .expectNextMatches(f -> "Nuevo".equals(f.nombre()))
                .verifyComplete();
    }
}

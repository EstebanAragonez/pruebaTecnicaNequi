package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddBranchUseCase")
class AddBranchUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    private AddBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddBranchUseCase(franchiseRepository, branchRepository);
    }

    @Test
    @DisplayName("crea sucursal exitosamente")
    void creaSucursalExitosamente() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        Branch saved = new Branch(1L, "Sucursal Centro", 1L, java.util.List.of());
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(1L, "Sucursal Centro"))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza franchiseId nulo")
    void rechazaFranchiseIdNulo() {
        StepVerifier.create(useCase.execute(null, "Sucursal"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza franquicia inexistente")
    void rechazaFranquiciaInexistente() {
        when(franchiseRepository.existsById(999L)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.execute(999L, "Sucursal"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza nombre vacío")
    void rechazaNombreVacio() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.execute(1L, "  "))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza nombre nulo")
    void rechazaNombreNulo() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.execute(1L, null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("trim del nombre antes de guardar")
    void trimNombreAntesDeGuardar() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        Branch saved = new Branch(1L, "Sucursal", 1L, java.util.List.of());
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(1L, "  Sucursal  "))
                .expectNextMatches(b -> "Sucursal".equals(b.nombre()))
                .verifyComplete();
    }
}

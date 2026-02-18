package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Franchise;
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
@DisplayName("AddFranchiseUseCase")
class AddFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private AddFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddFranchiseUseCase(franchiseRepository);
    }

    @Test
    @DisplayName("crea franquicia exitosamente")
    void creaFranquiciaExitosamente() {
        Franchise saved = new Franchise(1L, "Franquicia Test", java.util.List.of());
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute("Franquicia Test"))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza nombre nulo")
    void rechazaNombreNulo() {
        StepVerifier.create(useCase.execute(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza nombre en blanco")
    void rechazaNombreEnBlanco() {
        StepVerifier.create(useCase.execute("   "))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("trim el nombre antes de guardar")
    void trimNombreAntesDeGuardar() {
        Franchise saved = new Franchise(1L, "Nombre", java.util.List.of());
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute("  Nombre  "))
                .expectNextMatches(f -> "Nombre".equals(f.nombre()))
                .verifyComplete();
    }

    @Test
    @DisplayName("propaga error del repositorio")
    void propagaErrorRepositorio() {
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.execute("Test"))
                .expectError(RuntimeException.class)
                .verify();
    }
}

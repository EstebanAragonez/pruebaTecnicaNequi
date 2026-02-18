package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.port.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateBranchNameUseCase")
class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    private UpdateBranchNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateBranchNameUseCase(branchRepository);
    }

    @Test
    @DisplayName("actualiza nombre exitosamente")
    void actualizaNombreExitosamente() {
        Branch existing = new Branch(1L, "Viejo", 10L, java.util.List.of());
        Branch updated = new Branch(1L, "Nuevo", 10L, java.util.List.of());
        when(branchRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.execute(1L, "Nuevo"))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza sucursal no encontrada")
    void rechazaSucursalNoEncontrada() {
        when(branchRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(999L, "Nuevo"))
                .expectError(ResponseStatusException.class)
                .verify();
    }
}

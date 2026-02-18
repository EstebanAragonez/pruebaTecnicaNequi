package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteProductUseCase")
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProductUseCase(productRepository, branchRepository);
    }

    @Test
    @DisplayName("elimina producto exitosamente")
    void eliminaProductoExitosamente() {
        when(productRepository.existsByIdAndBranchId(1L, 5L)).thenReturn(Mono.just(true));
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(1L, 5L))
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza cuando producto no pertenece a sucursal")
    void rechazaProductoNoPerteneceSucursal() {
        when(productRepository.existsByIdAndBranchId(1L, 5L)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.execute(1L, 5L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza productId nulo")
    void rechazaProductIdNulo() {
        StepVerifier.create(useCase.execute(null, 5L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza branchId nulo")
    void rechazaBranchIdNulo() {
        StepVerifier.create(useCase.execute(1L, null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}

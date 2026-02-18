package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.ProductRepository;
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
@DisplayName("UpdateProductStockUseCase")
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private UpdateProductStockUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProductStockUseCase(productRepository);
    }

    @Test
    @DisplayName("actualiza stock exitosamente")
    void actualizaStockExitosamente() {
        Product existing = new Product(1L, "Producto", 10, 5L);
        Product updated = new Product(1L, "Producto", 20, 5L);
        when(productRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.execute(1L, 20))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza producto no encontrado")
    void rechazaProductoNoEncontrado() {
        when(productRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(999L, 10))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza stock negativo")
    void rechazaStockNegativo() {
        Product existing = new Product(1L, "Producto", 10, 5L);
        when(productRepository.findById(1L)).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.execute(1L, -1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza productId nulo")
    void rechazaProductIdNulo() {
        StepVerifier.create(useCase.execute(null, 10))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza newStock nulo")
    void rechazaNewStockNulo() {
        Product existing = new Product(1L, "Producto", 10, 5L);
        when(productRepository.findById(1L)).thenReturn(Mono.just(existing));

        StepVerifier.create(useCase.execute(1L, null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("permite stock cero")
    void permiteStockCero() {
        Product existing = new Product(1L, "Producto", 10, 5L);
        Product updated = new Product(1L, "Producto", 0, 5L);
        when(productRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.execute(1L, 0))
                .expectNextMatches(p -> p.stock() == 0)
                .verifyComplete();
    }
}

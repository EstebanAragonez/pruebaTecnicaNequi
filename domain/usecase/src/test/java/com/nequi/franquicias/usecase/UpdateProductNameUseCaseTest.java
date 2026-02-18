package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.ProductRepository;
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
@DisplayName("UpdateProductNameUseCase")
class UpdateProductNameUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private UpdateProductNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProductNameUseCase(productRepository);
    }

    @Test
    @DisplayName("actualiza nombre exitosamente")
    void actualizaNombreExitosamente() {
        Product existing = new Product(1L, "Viejo", 10, 5L);
        Product updated = new Product(1L, "Nuevo", 10, 5L);
        when(productRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.execute(1L, "Nuevo"))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza producto no encontrado")
    void rechazaProductoNoEncontrado() {
        when(productRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(999L, "Nuevo"))
                .expectError(ResponseStatusException.class)
                .verify();
    }
}

package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Product;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddProductUseCase")
class AddProductUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    private AddProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddProductUseCase(branchRepository, productRepository);
    }

    @Test
    @DisplayName("crea producto exitosamente")
    void creaProductoExitosamente() {
        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));
        Product saved = new Product(1L, "Producto", 10, 1L);
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(1L, "Producto", 10))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    @DisplayName("usa stock 0 por defecto cuando null")
    void usaStockCeroCuandoNull() {
        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));
        Product saved = new Product(1L, "Producto", 0, 1L);
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(1L, "Producto", null))
                .expectNextMatches(p -> p.stock() == 0)
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza sucursal inexistente")
    void rechazaSucursalInexistente() {
        when(branchRepository.existsById(999L)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.execute(999L, "Producto", 5))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza nombre vacío")
    void rechazaNombreVacio() {
        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.execute(1L, "  ", 5))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza stock negativo")
    void rechazaStockNegativo() {
        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.execute(1L, "Producto", -1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza branchId nulo")
    void rechazaBranchIdNulo() {
        StepVerifier.create(useCase.execute(null, "Producto", 5))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza nombre nulo")
    void rechazaNombreNulo() {
        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.execute(1L, null, 5))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("recorta espacios en nombre")
    void recortaEspaciosEnNombre() {
        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));
        Product saved = new Product(1L, "Producto", 10, 1L);
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.execute(1L, "  Producto  ", 10))
                .expectNextMatches(p -> "Producto".equals(p.nombre()))
                .verifyComplete();
    }
}

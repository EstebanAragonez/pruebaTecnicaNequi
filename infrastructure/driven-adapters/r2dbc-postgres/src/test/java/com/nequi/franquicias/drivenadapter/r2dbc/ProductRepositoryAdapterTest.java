package com.nequi.franquicias.drivenadapter.r2dbc;

import com.nequi.franquicias.drivenadapter.r2dbc.entity.ProductEntity;
import com.nequi.franquicias.drivenadapter.r2dbc.repository.ProductR2dbcRepository;
import com.nequi.franquicias.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductRepositoryAdapter")
class ProductRepositoryAdapterTest {

    @Mock
    private ProductR2dbcRepository r2dbcRepository;

    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductRepositoryAdapter(r2dbcRepository);
    }

    @Test
    @DisplayName("guarda producto")
    void guardaProducto() {
        ProductEntity entity = new ProductEntity(1L, "Producto", 10, 5L);
        when(r2dbcRepository.save(any(ProductEntity.class))).thenReturn(Mono.just(entity));

        Product input = new Product(null, "Producto", 10, 5L);

        StepVerifier.create(adapter.save(input))
                .expectNextMatches(p -> p.id() == 1L && p.stock() == 10)
                .verifyComplete();
    }

    @Test
    @DisplayName("busca por branchId")
    void buscaPorBranchId() {
        when(r2dbcRepository.findByBranchId(5L)).thenReturn(Flux.just(
                new ProductEntity(1L, "P1", 5, 5L),
                new ProductEntity(2L, "P2", 10, 5L)));

        StepVerifier.create(adapter.findByBranchId(5L))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("elimina por id")
    void eliminaPorId() {
        when(r2dbcRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("existe por id y branchId")
    void existePorIdYBranchId() {
        when(r2dbcRepository.existsByIdAndBranchId(1L, 5L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByIdAndBranchId(1L, 5L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("busca por id")
    void buscaPorId() {
        ProductEntity entity = new ProductEntity(1L, "Producto", 10, 5L);
        when(r2dbcRepository.findById(1L)).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(p -> p.id() == 1L && "Producto".equals(p.nombre()) && p.stock() == 10)
                .verifyComplete();
    }

    @Test
    @DisplayName("existe por id")
    void existePorId() {
        when(r2dbcRepository.existsById(1L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById retorna vacío cuando no existe")
    void findByIdRetornaVacioCuandoNoExiste() {
        when(r2dbcRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(999L))
                .verifyComplete();
    }
}

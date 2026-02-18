package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.FranchiseRepository;
import com.nequi.franquicias.model.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMaxStockProductsByFranchiseUseCase")
class GetMaxStockProductsByFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    private GetMaxStockProductsByFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetMaxStockProductsByFranchiseUseCase(
                franchiseRepository, branchRepository, productRepository);
    }

    @Test
    @DisplayName("obtiene productos con mayor stock por sucursal")
    void obtieneProductosMayorStock() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        Branch branch1 = new Branch(10L, "Sucursal A", 1L, List.of());
        Branch branch2 = new Branch(20L, "Sucursal B", 1L, List.of());
        when(branchRepository.findByFranchiseId(1L))
                .thenReturn(Flux.just(branch1, branch2));

        Product p1a = new Product(1L, "Prod A1", 5, 10L);
        Product p1b = new Product(2L, "Prod A2", 15, 10L);
        when(productRepository.findByBranchId(10L)).thenReturn(Flux.just(p1a, p1b));

        Product p2 = new Product(3L, "Prod B", 10, 20L);
        when(productRepository.findByBranchId(20L)).thenReturn(Flux.just(p2));

        StepVerifier.create(useCase.execute(1L))
                .expectNextMatches(list -> {
                    if (list.size() != 2) return false;
                    var s1 = list.stream().filter(p -> p.branchId() == 10L).findFirst().orElseThrow();
                    var s2 = list.stream().filter(p -> p.branchId() == 20L).findFirst().orElseThrow();
                    return s1.stock() == 15 && s1.productName().equals("Prod A2")
                            && s2.stock() == 10 && s2.productName().equals("Prod B");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("omite sucursales sin productos")
    void omiteSucursalesSinProductos() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        Branch branchVacia = new Branch(10L, "Sucursal vacía", 1L, List.of());
        when(branchRepository.findByFranchiseId(1L)).thenReturn(Flux.just(branchVacia));
        when(productRepository.findByBranchId(10L)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute(1L))
                .expectNext(List.of())
                .verifyComplete();
    }

    @Test
    @DisplayName("rechaza franchiseId nulo")
    void rechazaFranchiseIdNulo() {
        StepVerifier.create(useCase.execute(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("rechaza franquicia inexistente")
    void rechazaFranquiciaInexistente() {
        when(franchiseRepository.existsById(999L)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.execute(999L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    @DisplayName("retorna lista vacía cuando franquicia no tiene sucursales")
    void retornaListaVaciaSinSucursales() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(branchRepository.findByFranchiseId(1L)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.execute(1L))
                .expectNext(List.of())
                .verifyComplete();
    }

    @Test
    @DisplayName("cuando productos tienen stock igual reduce mantiene el primero")
    void productosConStockIgualReduceMantienePrimero() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        Branch branch = new Branch(10L, "Sucursal", 1L, List.of());
        when(branchRepository.findByFranchiseId(1L)).thenReturn(Flux.just(branch));

        Product p1 = new Product(1L, "Prod A", 10, 10L);
        Product p2 = new Product(2L, "Prod B", 10, 10L);
        when(productRepository.findByBranchId(10L)).thenReturn(Flux.just(p1, p2));

        StepVerifier.create(useCase.execute(1L))
                .expectNextMatches(list -> list.size() == 1 && list.get(0).stock() == 10)
                .verifyComplete();
    }

    @Test
    @DisplayName("propaga error cuando franchiseRepository falla")
    void propagaErrorCuandoFranchiseRepositoryFalla() {
        when(franchiseRepository.existsById(1L)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.execute(1L))
                .expectError(RuntimeException.class)
                .verify();
    }
}

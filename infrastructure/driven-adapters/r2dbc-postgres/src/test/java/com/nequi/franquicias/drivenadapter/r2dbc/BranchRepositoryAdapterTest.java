package com.nequi.franquicias.drivenadapter.r2dbc;

import com.nequi.franquicias.drivenadapter.r2dbc.entity.BranchEntity;
import com.nequi.franquicias.drivenadapter.r2dbc.repository.BranchR2dbcRepository;
import com.nequi.franquicias.model.Branch;
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
@DisplayName("BranchRepositoryAdapter")
class BranchRepositoryAdapterTest {

    @Mock
    private BranchR2dbcRepository r2dbcRepository;

    private BranchRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchRepositoryAdapter(r2dbcRepository);
    }

    @Test
    @DisplayName("guarda sucursal")
    void guardaSucursal() {
        BranchEntity entity = new BranchEntity(1L, "Sucursal", 10L);
        when(r2dbcRepository.save(any(BranchEntity.class))).thenReturn(Mono.just(entity));

        Branch input = new Branch(null, "Sucursal", 10L, java.util.List.of());

        StepVerifier.create(adapter.save(input))
                .expectNextMatches(b -> b.id() == 1L && b.franchiseId() == 10L)
                .verifyComplete();
    }

    @Test
    @DisplayName("busca por franchiseId")
    void buscaPorFranchiseId() {
        when(r2dbcRepository.findByFranchiseId(10L)).thenReturn(Flux.just(
                new BranchEntity(1L, "Sucursal A", 10L),
                new BranchEntity(2L, "Sucursal B", 10L)));

        StepVerifier.create(adapter.findByFranchiseId(10L))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    @DisplayName("existe por id y franchiseId")
    void existePorIdYFranchiseId() {
        when(r2dbcRepository.existsByIdAndFranchiseId(1L, 10L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByIdAndFranchiseId(1L, 10L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("busca por id")
    void buscaPorId() {
        BranchEntity entity = new BranchEntity(1L, "Sucursal Centro", 10L);
        when(r2dbcRepository.findById(1L)).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(b -> b.id() == 1L && "Sucursal Centro".equals(b.nombre()))
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

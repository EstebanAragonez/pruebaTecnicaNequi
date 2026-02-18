package com.nequi.franquicias.drivenadapter.r2dbc;

import com.nequi.franquicias.drivenadapter.r2dbc.entity.FranchiseEntity;
import com.nequi.franquicias.drivenadapter.r2dbc.repository.FranchiseR2dbcRepository;
import com.nequi.franquicias.model.Franchise;
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
@DisplayName("FranchiseRepositoryAdapter")
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseR2dbcRepository r2dbcRepository;

    private FranchiseRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchiseRepositoryAdapter(r2dbcRepository);
    }

    @Test
    @DisplayName("guarda franquicia y mapea a dominio")
    void guardaFranquicia() {
        FranchiseEntity entity = new FranchiseEntity(1L, "Franquicia Test");
        when(r2dbcRepository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(entity));

        Franchise input = new Franchise(null, "Franquicia Test", java.util.List.of());

        StepVerifier.create(adapter.save(input))
                .expectNextMatches(f -> f.id() == 1L && "Franquicia Test".equals(f.nombre()))
                .verifyComplete();
    }

    @Test
    @DisplayName("busca por id")
    void buscaPorId() {
        FranchiseEntity entity = new FranchiseEntity(1L, "Franquicia");
        when(r2dbcRepository.findById(1L)).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(f -> f.id() == 1L)
                .verifyComplete();
    }

    @Test
    @DisplayName("busca todos")
    void buscaTodos() {
        when(r2dbcRepository.findAll()).thenReturn(Flux.just(
                new FranchiseEntity(1L, "A"),
                new FranchiseEntity(2L, "B")));

        StepVerifier.create(adapter.findAll())
                .expectNextCount(2)
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

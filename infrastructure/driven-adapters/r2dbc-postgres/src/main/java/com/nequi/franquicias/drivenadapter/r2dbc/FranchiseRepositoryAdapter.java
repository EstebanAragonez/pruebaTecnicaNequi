package com.nequi.franquicias.drivenadapter.r2dbc;

import com.nequi.franquicias.model.Franchise;
import com.nequi.franquicias.model.port.FranchiseRepository;
import com.nequi.franquicias.drivenadapter.r2dbc.entity.FranchiseEntity;
import com.nequi.franquicias.drivenadapter.r2dbc.repository.FranchiseR2dbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Adaptador de persistencia que implementa el puerto FranchiseRepository.
 * Traduce entre entidades de BD y modelos de dominio.
 */
@Repository
public class FranchiseRepositoryAdapter implements FranchiseRepository {

    private static final Logger log = LoggerFactory.getLogger(FranchiseRepositoryAdapter.class);

    private final FranchiseR2dbcRepository r2dbcRepository;

    public FranchiseRepositoryAdapter(FranchiseR2dbcRepository r2dbcRepository) {
        this.r2dbcRepository = r2dbcRepository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = new FranchiseEntity(franchise.id(), franchise.nombre());
        return r2dbcRepository.save(entity)
                .map(this::toDomain)
                .doOnSuccess(f -> log.debug("Franquicia guardada: id={}", f.id()))
                .doOnError(e -> log.error("Error guardando franquicia: {}", e.getMessage()));
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return r2dbcRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        return r2dbcRepository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return r2dbcRepository.existsById(id);
    }

    private Franchise toDomain(FranchiseEntity entity) {
        return new Franchise(entity.id(), entity.nombre(), Collections.emptyList());
    }
}

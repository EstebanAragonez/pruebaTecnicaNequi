package com.nequi.franquicias.drivenadapter.r2dbc;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.drivenadapter.r2dbc.entity.BranchEntity;
import com.nequi.franquicias.drivenadapter.r2dbc.repository.BranchR2dbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Adaptador de persistencia para sucursales.
 */
@Repository
public class BranchRepositoryAdapter implements BranchRepository {

    private static final Logger log = LoggerFactory.getLogger(BranchRepositoryAdapter.class);

    private final BranchR2dbcRepository r2dbcRepository;

    public BranchRepositoryAdapter(BranchR2dbcRepository r2dbcRepository) {
        this.r2dbcRepository = r2dbcRepository;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = new BranchEntity(branch.id(), branch.nombre(), branch.franchiseId());
        return r2dbcRepository.save(entity)
                .map(this::toDomain)
                .doOnSuccess(b -> log.debug("Sucursal guardada: id={}", b.id()))
                .doOnError(e -> log.error("Error guardando sucursal: {}", e.getMessage()));
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return r2dbcRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Flux<Branch> findByFranchiseId(Long franchiseId) {
        return r2dbcRepository.findByFranchiseId(franchiseId)
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return r2dbcRepository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByIdAndFranchiseId(Long id, Long franchiseId) {
        return r2dbcRepository.existsByIdAndFranchiseId(id, franchiseId);
    }

    private Branch toDomain(BranchEntity entity) {
        return new Branch(entity.id(), entity.nombre(), entity.franchiseId(), Collections.emptyList());
    }
}

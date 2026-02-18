package com.nequi.franquicias.drivenadapter.r2dbc.repository;

import com.nequi.franquicias.drivenadapter.r2dbc.entity.BranchEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchR2dbcRepository extends R2dbcRepository<BranchEntity, Long> {

    Flux<BranchEntity> findByFranchiseId(Long franchiseId);
    Mono<Boolean> existsById(Long id);
    Mono<Boolean> existsByIdAndFranchiseId(Long id, Long franchiseId);
}

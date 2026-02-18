package com.nequi.franquicias.drivenadapter.r2dbc.repository;

import com.nequi.franquicias.drivenadapter.r2dbc.entity.FranchiseEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface FranchiseR2dbcRepository extends R2dbcRepository<FranchiseEntity, Long> {

    Mono<Boolean> existsById(Long id);
}

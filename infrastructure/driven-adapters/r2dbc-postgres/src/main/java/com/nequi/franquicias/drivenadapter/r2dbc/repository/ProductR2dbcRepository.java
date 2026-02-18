package com.nequi.franquicias.drivenadapter.r2dbc.repository;

import com.nequi.franquicias.drivenadapter.r2dbc.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository extends R2dbcRepository<ProductEntity, Long> {

    Flux<ProductEntity> findByBranchId(Long branchId);
    Mono<Boolean> existsById(Long id);
    Mono<Boolean> existsByIdAndBranchId(Long id, Long branchId);
}

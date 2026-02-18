package com.nequi.franquicias.model.port;

import com.nequi.franquicias.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para persistencia de sucursales.
 */
public interface BranchRepository {

    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(Long id);
    Flux<Branch> findByFranchiseId(Long franchiseId);
    Mono<Boolean> existsById(Long id);
    Mono<Boolean> existsByIdAndFranchiseId(Long id, Long franchiseId);
}

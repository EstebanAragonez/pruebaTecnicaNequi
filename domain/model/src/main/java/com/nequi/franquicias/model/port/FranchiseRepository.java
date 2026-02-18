package com.nequi.franquicias.model.port;

import com.nequi.franquicias.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para persistencia de franquicias.
 * Principio de Inversión de Dependencias - el dominio define el contrato.
 */
public interface FranchiseRepository {

    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(Long id);
    Flux<Franchise> findAll();
    Mono<Boolean> existsById(Long id);
}

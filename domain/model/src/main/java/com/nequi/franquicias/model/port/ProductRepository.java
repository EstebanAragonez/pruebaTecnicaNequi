package com.nequi.franquicias.model.port;

import com.nequi.franquicias.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida para persistencia de productos.
 */
public interface ProductRepository {

    Mono<Product> save(Product product);
    Mono<Product> findById(Long id);
    Flux<Product> findByBranchId(Long branchId);
    Mono<Void> deleteById(Long id);
    Mono<Boolean> existsById(Long id);
    Mono<Boolean> existsByIdAndBranchId(Long id, Long branchId);
}

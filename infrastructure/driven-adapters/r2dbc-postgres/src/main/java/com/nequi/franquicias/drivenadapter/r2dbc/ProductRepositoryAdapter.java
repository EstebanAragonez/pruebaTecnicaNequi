package com.nequi.franquicias.drivenadapter.r2dbc;

import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.ProductRepository;
import com.nequi.franquicias.drivenadapter.r2dbc.entity.ProductEntity;
import com.nequi.franquicias.drivenadapter.r2dbc.repository.ProductR2dbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adaptador de persistencia para productos.
 */
@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductRepositoryAdapter.class);

    private final ProductR2dbcRepository r2dbcRepository;

    public ProductRepositoryAdapter(ProductR2dbcRepository r2dbcRepository) {
        this.r2dbcRepository = r2dbcRepository;
    }

    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = new ProductEntity(product.id(), product.nombre(), product.stock(), product.branchId());
        return r2dbcRepository.save(entity)
                .map(this::toDomain)
                .doOnSuccess(p -> log.debug("Producto guardado: id={}", p.id()))
                .doOnError(e -> log.error("Error guardando producto: {}", e.getMessage()));
    }

    @Override
    public Mono<Product> findById(Long id) {
        return r2dbcRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Flux<Product> findByBranchId(Long branchId) {
        return r2dbcRepository.findByBranchId(branchId)
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return r2dbcRepository.deleteById(id)
                .doOnSuccess(v -> log.debug("Producto eliminado: id={}", id))
                .doOnError(e -> log.error("Error eliminando producto: {}", e.getMessage()));
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return r2dbcRepository.existsById(id);
    }

    @Override
    public Mono<Boolean> existsByIdAndBranchId(Long id, Long branchId) {
        return r2dbcRepository.existsByIdAndBranchId(id, branchId);
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(entity.id(), entity.nombre(), entity.stock(), entity.branchId());
    }
}

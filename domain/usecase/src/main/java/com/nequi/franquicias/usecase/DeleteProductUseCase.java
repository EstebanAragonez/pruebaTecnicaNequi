package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Eliminar un producto de una sucursal.
 * Valida que el producto pertenezca a la sucursal antes de eliminar.
 */
public class DeleteProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeleteProductUseCase.class);

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public DeleteProductUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
    }

    public Mono<Void> execute(Long productId, Long branchId) {
        return Mono.justOrEmpty(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID del producto es requerido")))
                .flatMap(pid -> Mono.justOrEmpty(branchId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID de sucursal es requerido")))
                        .flatMap(bid -> productRepository.existsByIdAndBranchId(pid, bid)
                                .flatMap(exists -> Boolean.TRUE.equals(exists)
                                        ? productRepository.deleteById(pid)
                                        : Mono.error(new IllegalArgumentException("Producto no encontrado en la sucursal indicada")))))
                .doOnSuccess(v -> log.info("Producto eliminado: productId={}, branchId={}", productId, branchId))
                .doOnError(e -> log.error("Error eliminando producto: {}", e.getMessage()))
                .onErrorResume(IllegalArgumentException.class, Mono::error);
    }
}

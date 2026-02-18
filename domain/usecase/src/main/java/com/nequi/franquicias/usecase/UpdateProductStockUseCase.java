package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Modificar el stock de un producto.
 * Usa flatMap para encadenar búsqueda y actualización.
 */
public class UpdateProductStockUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductStockUseCase.class);

    private final ProductRepository productRepository;

    public UpdateProductStockUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Product> execute(Long productId, Integer newStock) {
        return Mono.justOrEmpty(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID del producto es requerido")))
                .flatMap(id -> productRepository.findById(id)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado: " + id))))
                .flatMap(product -> Mono.justOrEmpty(newStock)
                        .filter(s -> s >= 0)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El stock no puede ser negativo")))
                        .map(product::withStock)
                        .flatMap(productRepository::save))
                .doOnNext(p -> log.info("Stock actualizado: productId={}, nuevoStock={}", p.id(), p.stock()))
                .doOnError(e -> log.error("Error actualizando stock: {}", e.getMessage()))
                .onErrorResume(IllegalArgumentException.class, Mono::error);
    }
}

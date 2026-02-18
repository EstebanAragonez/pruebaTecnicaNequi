package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Agregar un nuevo producto a una sucursal.
 * Encadena validaciones con flatMap, switchIfEmpty.
 */
public class AddProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(AddProductUseCase.class);

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public AddProductUseCase(BranchRepository branchRepository, ProductRepository productRepository) {
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    public Mono<Product> execute(Long branchId, String nombre, Integer stock) {
        return Mono.justOrEmpty(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID de sucursal es requerido")))
                .flatMap(id -> branchRepository.existsById(id)
                        .flatMap(exists -> Boolean.TRUE.equals(exists)
                                ? Mono.just(id)
                                : Mono.error(new IllegalArgumentException("Sucursal no encontrada: " + id))))
                .flatMap(bId -> Mono.justOrEmpty(nombre)
                        .filter(n -> n != null && !n.isBlank())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El nombre del producto es requerido")))
                        .zipWith(Mono.justOrEmpty(stock)
                                .defaultIfEmpty(0)
                                .filter(s -> s >= 0)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException("El stock no puede ser negativo"))))
                        .map(tuple -> new Product(null, tuple.getT1().trim(), tuple.getT2(), bId))
                        .flatMap(productRepository::save))
                .doOnNext(p -> log.info("Producto creado: id={}, nombre={}, branchId={}", p.id(), p.nombre(), p.branchId()))
                .doOnError(e -> log.error("Error creando producto: {}", e.getMessage()))
                .onErrorResume(IllegalArgumentException.class, Mono::error);
    }
}

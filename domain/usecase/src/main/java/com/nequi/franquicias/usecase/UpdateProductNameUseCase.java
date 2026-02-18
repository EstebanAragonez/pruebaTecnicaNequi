package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Actualizar el nombre de un producto.
 */
public class UpdateProductNameUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductNameUseCase.class);

    private final ProductRepository productRepository;

    public UpdateProductNameUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Product> execute(Long productId, String newNombre) {
        return Mono.justOrEmpty(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID del producto es requerido")))
                .flatMap(id -> productRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id))))
                .flatMap(product -> Mono.justOrEmpty(newNombre)
                        .filter(n -> n != null && !n.isBlank())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El nombre del producto es requerido")))
                        .map(name -> new Product(product.id(), name.trim(), product.stock(), product.branchId()))
                        .flatMap(productRepository::save))
                .doOnNext(p -> log.info("Producto actualizado: id={}, nuevoNombre={}", p.id(), p.nombre()))
                .doOnError(e -> log.error("Error actualizando producto: {}", e.getMessage()));
    }
}

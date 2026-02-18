package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.FranchiseRepository;
import com.nequi.franquicias.model.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Caso de uso: Obtener el producto con más stock por sucursal para una franquicia.
 * Retorna listado de productos indicando a qué sucursal pertenece cada uno.
 * Encadena flujos con operadores: flatMap, map, switchIfEmpty, reduce, collectList.
 * Usa señales onNext, onError, onComplete correctamente.
 */
public class GetMaxStockProductsByFranchiseUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetMaxStockProductsByFranchiseUseCase.class);

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public GetMaxStockProductsByFranchiseUseCase(FranchiseRepository franchiseRepository,
                                                 BranchRepository branchRepository,
                                                 ProductRepository productRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    /**
     * DTO interno para el producto con información de sucursal.
     */
    public record ProductWithBranch(Long productId, String productName, Integer stock, Long branchId, String branchName) {}

    public Mono<List<ProductWithBranch>> execute(Long franchiseId) {
        return Mono.justOrEmpty(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID de franquicia es requerido")))
                .flatMap(id -> franchiseRepository.existsById(id)
                        .flatMap(exists -> Boolean.TRUE.equals(exists)
                                ? Mono.just(id)
                                : Mono.error(new IllegalArgumentException("Franquicia no encontrada: " + id))))
                .flatMapMany(branchRepository::findByFranchiseId)
                .flatMap(this::findMaxStockProductInBranch)
                .collectList()
                .doOnNext(list -> log.info("Productos con mayor stock por sucursal: franchiseId={}, cantidad={}", franchiseId, list.size()))
                .doOnError(e -> log.error("Error obteniendo productos con mayor stock: {}", e.getMessage()))
                .doOnSuccess(list -> log.debug("Flujo completado para franchiseId={}", franchiseId));
    }

    /**
     * Encuentra el producto con mayor stock en una sucursal.
     * Usa reduce para encadenar y comparar productos, map para transformar,
     * switchIfEmpty para manejar sucursales sin productos.
     */
    private Mono<ProductWithBranch> findMaxStockProductInBranch(Branch branch) {
        return productRepository.findByBranchId(branch.id())
                .reduce((a, b) -> a.stock() >= b.stock() ? a : b)
                .map(product -> new ProductWithBranch(
                        product.id(),
                        product.nombre(),
                        product.stock(),
                        branch.id(),
                        branch.nombre()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Sucursal {} no tiene productos, omitiendo", branch.nombre());
                    return Mono.empty();
                }))
                .doOnNext(pwb -> log.debug("Producto con mayor stock: branch={}, product={}, stock={}",
                        branch.nombre(), pwb.productName(), pwb.stock()))
                .doOnError(e -> log.error("Error procesando sucursal {}: {}", branch.nombre(), e.getMessage()));
    }
}

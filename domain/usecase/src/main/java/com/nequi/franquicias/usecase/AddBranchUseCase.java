package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.FranchiseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Caso de uso: Agregar una nueva sucursal a una franquicia.
 * Encadena flujos con flatMap, switchIfEmpty.
 */
public class AddBranchUseCase {

    private static final Logger log = LoggerFactory.getLogger(AddBranchUseCase.class);

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public AddBranchUseCase(FranchiseRepository franchiseRepository, BranchRepository branchRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
    }

    public Mono<Branch> execute(Long franchiseId, String nombre) {
        return Mono.justOrEmpty(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID de franquicia es requerido")))
                .flatMap(id -> franchiseRepository.existsById(id)
                        .flatMap(exists -> Boolean.TRUE.equals(exists)
                                ? Mono.just(id)
                                : Mono.error(new IllegalArgumentException("Franquicia no encontrada: " + id))))
                .flatMap(fId -> Mono.justOrEmpty(nombre)
                        .filter(n -> n != null && !n.isBlank())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El nombre de la sucursal es requerido")))
                        .map(name -> new Branch(null, name.trim(), fId, Collections.emptyList()))
                        .flatMap(branchRepository::save))
                .doOnNext(b -> log.info("Sucursal creada: id={}, nombre={}, franchiseId={}", b.id(), b.nombre(), b.franchiseId()))
                .doOnError(e -> log.error("Error creando sucursal: {}", e.getMessage()))
                .onErrorResume(IllegalArgumentException.class, Mono::error);
    }
}

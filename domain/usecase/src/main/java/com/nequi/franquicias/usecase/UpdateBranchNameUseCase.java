package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.port.BranchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Actualizar el nombre de una sucursal.
 */
public class UpdateBranchNameUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateBranchNameUseCase.class);

    private final BranchRepository branchRepository;

    public UpdateBranchNameUseCase(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public Mono<Branch> execute(Long branchId, String newNombre) {
        return Mono.justOrEmpty(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID de la sucursal es requerido")))
                .flatMap(id -> branchRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada: " + id))))
                .flatMap(branch -> Mono.justOrEmpty(newNombre)
                        .filter(n -> n != null && !n.isBlank())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El nombre de la sucursal es requerido")))
                        .map(name -> new Branch(branch.id(), name.trim(), branch.franchiseId(), branch.productos()))
                        .flatMap(branchRepository::save))
                .doOnNext(b -> log.info("Sucursal actualizada: id={}, nuevoNombre={}", b.id(), b.nombre()))
                .doOnError(e -> log.error("Error actualizando sucursal: {}", e.getMessage()));
    }
}

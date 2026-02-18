package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Franchise;
import com.nequi.franquicias.model.port.FranchiseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Actualizar el nombre de una franquicia.
 */
public class UpdateFranchiseNameUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateFranchiseNameUseCase.class);

    private final FranchiseRepository franchiseRepository;

    public UpdateFranchiseNameUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    public Mono<Franchise> execute(Long franchiseId, String newNombre) {
        return Mono.justOrEmpty(franchiseId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El ID de la franquicia es requerido")))
                .flatMap(id -> franchiseRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Franquicia no encontrada: " + id))))
                .flatMap(franchise -> Mono.justOrEmpty(newNombre)
                        .filter(n -> n != null && !n.isBlank())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("El nombre de la franquicia es requerido")))
                        .map(name -> new Franchise(franchise.id(), name.trim(), franchise.sucursales()))
                        .flatMap(franchiseRepository::save))
                .doOnNext(f -> log.info("Franquicia actualizada: id={}, nuevoNombre={}", f.id(), f.nombre()))
                .doOnError(e -> log.error("Error actualizando franquicia: {}", e.getMessage()));
    }
}

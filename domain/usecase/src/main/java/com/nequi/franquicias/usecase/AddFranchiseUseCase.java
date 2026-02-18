package com.nequi.franquicias.usecase;

import com.nequi.franquicias.model.Franchise;
import com.nequi.franquicias.model.port.FranchiseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

/**
 * Caso de uso: Agregar una nueva franquicia.
 * Usa operadores reactivos y señales onNext, onError, onComplete.
 */
public class AddFranchiseUseCase {

    private static final Logger log = LoggerFactory.getLogger(AddFranchiseUseCase.class);

    private final FranchiseRepository franchiseRepository;

    public AddFranchiseUseCase(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    public Mono<Franchise> execute(String nombre) {
        return Mono.justOrEmpty(nombre)
                .filter(n -> n != null && !n.isBlank())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("El nombre de la franquicia es requerido")))
                .map(name -> new Franchise(null, name.trim(), java.util.Collections.emptyList()))
                .flatMap(franchiseRepository::save)
                .doOnNext(f -> log.info("Franquicia creada: id={}, nombre={}", f.id(), f.nombre()))
                .doOnError(e -> log.error("Error creando franquicia: {}", e.getMessage()))
                .onErrorResume(IllegalArgumentException.class, Mono::error);
    }
}

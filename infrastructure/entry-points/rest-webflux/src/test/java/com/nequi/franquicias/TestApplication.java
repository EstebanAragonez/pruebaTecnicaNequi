package com.nequi.franquicias;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación mínima para tests. Permite que @WebFluxTest encuentre @SpringBootConfiguration
 * en el paquete com.nequi.franquicias cuando se ejecutan tests en rest-webflux.
 */
@SpringBootApplication(scanBasePackages = "com.nequi.franquicias.entrypoint")
public class TestApplication {
}

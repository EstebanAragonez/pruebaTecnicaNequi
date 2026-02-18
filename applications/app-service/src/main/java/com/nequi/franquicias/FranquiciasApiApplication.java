package com.nequi.franquicias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Módulo de aplicación - Bootable Bundle.
 * Punto de arranque de la aplicación.
 * Ensambla los módulos entry-points y driven-adapters.
 */
@SpringBootApplication(scanBasePackages = {
        "com.nequi.franquicias.entrypoint",
        "com.nequi.franquicias.drivenadapter",
        "com.nequi.franquicias.config"
})
public class FranquiciasApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FranquiciasApiApplication.class, args);
    }
}

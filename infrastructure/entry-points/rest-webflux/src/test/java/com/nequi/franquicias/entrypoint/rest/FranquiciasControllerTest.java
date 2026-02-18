package com.nequi.franquicias.entrypoint.rest;

import com.nequi.franquicias.model.Branch;
import com.nequi.franquicias.model.Franchise;
import com.nequi.franquicias.model.Product;
import com.nequi.franquicias.usecase.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = FranquiciasController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("FranquiciasController")
class FranquiciasControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AddFranchiseUseCase addFranchiseUseCase;
    @MockBean
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    @MockBean
    private AddBranchUseCase addBranchUseCase;
    @MockBean
    private UpdateBranchNameUseCase updateBranchNameUseCase;
    @MockBean
    private AddProductUseCase addProductUseCase;
    @MockBean
    private UpdateProductNameUseCase updateProductNameUseCase;
    @MockBean
    private DeleteProductUseCase deleteProductUseCase;
    @MockBean
    private UpdateProductStockUseCase updateProductStockUseCase;
    @MockBean
    private GetMaxStockProductsByFranchiseUseCase getMaxStockProductsUseCase;

    @Test
    @DisplayName("POST /franquicias retorna 201")
    void crearFranquiciaRetorna201() {
        Franchise franchise = new Franchise(1L, "Franquicia Test", List.of());
        when(addFranchiseUseCase.execute(any())).thenReturn(Mono.just(franchise));

        webTestClient.post().uri("/api/v1/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"nombre\":\"Franquicia Test\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombre").isEqualTo("Franquicia Test");
    }

    @Test
    @DisplayName("POST /sucursales retorna 201")
    void crearSucursalRetorna201() {
        Branch branch = new Branch(1L, "Sucursal Centro", 10L, List.of());
        when(addBranchUseCase.execute(anyLong(), any())).thenReturn(Mono.just(branch));

        webTestClient.post().uri("/api/v1/sucursales")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"franchiseId\":10,\"nombre\":\"Sucursal Centro\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.franchiseId").isEqualTo(10);
    }

    @Test
    @DisplayName("POST /productos retorna 201")
    void crearProductoRetorna201() {
        Product product = new Product(1L, "Producto", 10, 5L);
        when(addProductUseCase.execute(anyLong(), any(), anyInt())).thenReturn(Mono.just(product));

        webTestClient.post().uri("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"branchId\":5,\"nombre\":\"Producto\",\"stock\":10}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.stock").isEqualTo(10);
    }

    @Test
    @DisplayName("DELETE /sucursales/{branchId}/productos/{productId} retorna 204")
    void eliminarProductoRetorna204() {
        when(deleteProductUseCase.execute(1L, 5L)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/sucursales/5/productos/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("GET /franquicias/{id}/productos-mayor-stock retorna 200")
    void obtenerProductosMayorStockRetorna200() {
        var pwb = new GetMaxStockProductsByFranchiseUseCase.ProductWithBranch(1L, "Prod", 10, 5L, "Sucursal");
        when(getMaxStockProductsUseCase.execute(1L)).thenReturn(Mono.just(List.of(pwb)));

        webTestClient.get().uri("/api/v1/franquicias/1/productos-mayor-stock")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.franchiseId").isEqualTo(1)
                .jsonPath("$.productos[0].productId").isEqualTo(1)
                .jsonPath("$.productos[0].stock").isEqualTo(10);
    }

    @Test
    @DisplayName("PATCH /franquicias/{id}/nombre retorna 200")
    void actualizarNombreFranquiciaRetorna200() {
        Franchise franchise = new Franchise(1L, "Franquicia Actualizada", List.of());
        when(updateFranchiseNameUseCase.execute(1L, "Franquicia Actualizada")).thenReturn(Mono.just(franchise));

        webTestClient.patch().uri("/api/v1/franquicias/1/nombre")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"nombre\":\"Franquicia Actualizada\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombre").isEqualTo("Franquicia Actualizada");
    }

    @Test
    @DisplayName("PATCH /sucursales/{id}/nombre retorna 200")
    void actualizarNombreSucursalRetorna200() {
        Branch branch = new Branch(1L, "Sucursal Norte", 10L, List.of());
        when(updateBranchNameUseCase.execute(1L, "Sucursal Norte")).thenReturn(Mono.just(branch));

        webTestClient.patch().uri("/api/v1/sucursales/1/nombre")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"nombre\":\"Sucursal Norte\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombre").isEqualTo("Sucursal Norte");
    }

    @Test
    @DisplayName("PATCH /productos/{id}/nombre retorna 200")
    void actualizarNombreProductoRetorna200() {
        Product product = new Product(1L, "Producto Actualizado", 10, 5L);
        when(updateProductNameUseCase.execute(1L, "Producto Actualizado")).thenReturn(Mono.just(product));

        webTestClient.patch().uri("/api/v1/productos/1/nombre")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"nombre\":\"Producto Actualizado\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombre").isEqualTo("Producto Actualizado");
    }

    @Test
    @DisplayName("PATCH /productos/{id}/stock retorna 200")
    void actualizarStockProductoRetorna200() {
        Product product = new Product(1L, "Producto", 25, 5L);
        when(updateProductStockUseCase.execute(1L, 25)).thenReturn(Mono.just(product));

        webTestClient.patch().uri("/api/v1/productos/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":25}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.stock").isEqualTo(25);
    }

    @Test
    @DisplayName("POST /franquicias con body inválido retorna 400")
    void crearFranquiciaConBodyInvalidoRetorna400() {
        webTestClient.post().uri("/api/v1/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Error de validación");
    }

    @Test
    @DisplayName("GET /franquicias/0/productos-mayor-stock retorna 400 (ID inválido)")
    void obtenerProductosConIdInvalidoRetorna400() {
        webTestClient.get().uri("/api/v1/franquicias/0/productos-mayor-stock")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Error de validación");
    }
}

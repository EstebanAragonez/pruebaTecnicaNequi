package com.nequi.franquicias.entrypoint.rest;

import com.nequi.franquicias.entrypoint.rest.dto.*;
import com.nequi.franquicias.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Adaptador REST para la API de franquicias.
 * Punto de entrada que traduce peticiones HTTP a llamadas a la capa de casos de uso.
 */
@Tag(name = "Franquicias", description = "API de gestión de franquicias, sucursales y productos")
@Validated
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class FranquiciasController {

    private static final Logger log = LoggerFactory.getLogger(FranquiciasController.class);

    private final AddFranchiseUseCase addFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final AddBranchUseCase addBranchUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final AddProductUseCase addProductUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final GetMaxStockProductsByFranchiseUseCase getMaxStockProductsUseCase;

    public FranquiciasController(AddFranchiseUseCase addFranchiseUseCase,
                                  UpdateFranchiseNameUseCase updateFranchiseNameUseCase,
                                  AddBranchUseCase addBranchUseCase,
                                  UpdateBranchNameUseCase updateBranchNameUseCase,
                                  AddProductUseCase addProductUseCase,
                                  UpdateProductNameUseCase updateProductNameUseCase,
                                  DeleteProductUseCase deleteProductUseCase,
                                  UpdateProductStockUseCase updateProductStockUseCase,
                                  GetMaxStockProductsByFranchiseUseCase getMaxStockProductsUseCase) {
        this.addFranchiseUseCase = addFranchiseUseCase;
        this.updateFranchiseNameUseCase = updateFranchiseNameUseCase;
        this.addBranchUseCase = addBranchUseCase;
        this.updateBranchNameUseCase = updateBranchNameUseCase;
        this.addProductUseCase = addProductUseCase;
        this.updateProductNameUseCase = updateProductNameUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.getMaxStockProductsUseCase = getMaxStockProductsUseCase;
    }

    @Operation(summary = "Crear franquicia", description = "Registra una nueva franquicia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Franquicia creada", content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación")
    })
    @PostMapping("/franquicias")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FranchiseResponse> addFranchise(@Valid @RequestBody FranchiseRequest request) {
        log.debug("POST /franquicias - nombre: {}", request.nombre());
        return addFranchiseUseCase.execute(request.nombre())
                .map(f -> new FranchiseResponse(f.id(), f.nombre()));
    }

    @Operation(summary = "Actualizar nombre de franquicia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre actualizado", content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
    })
    @PatchMapping("/franquicias/{franchiseId}/nombre")
    public Mono<FranchiseResponse> updateFranchiseName(
            @PathVariable("franchiseId") @Positive(message = "El ID de franquicia debe ser positivo") Long franchiseId,
            @Valid @RequestBody FranchiseNameUpdateRequest request) {
        log.debug("PATCH /franquicias/{}/nombre - nombre: {}", franchiseId, request.nombre());
        return updateFranchiseNameUseCase.execute(franchiseId, request.nombre())
                .map(f -> new FranchiseResponse(f.id(), f.nombre()));
    }

    @Operation(summary = "Crear sucursal", description = "Registra una nueva sucursal en una franquicia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucursal creada", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
    })
    @PostMapping("/sucursales")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BranchResponse> addBranch(@Valid @RequestBody BranchRequest request) {
        log.debug("POST /sucursales - franchiseId: {}, nombre: {}", request.franchiseId(), request.nombre());
        return addBranchUseCase.execute(request.franchiseId(), request.nombre())
                .map(b -> new BranchResponse(b.id(), b.nombre(), b.franchiseId()));
    }

    @Operation(summary = "Actualizar nombre de sucursal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre actualizado", content = @Content(schema = @Schema(implementation = BranchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @PatchMapping("/sucursales/{branchId}/nombre")
    public Mono<BranchResponse> updateBranchName(
            @PathVariable("branchId") @Positive(message = "El ID de sucursal debe ser positivo") Long branchId,
            @Valid @RequestBody BranchNameUpdateRequest request) {
        log.debug("PATCH /sucursales/{}/nombre - nombre: {}", branchId, request.nombre());
        return updateBranchNameUseCase.execute(branchId, request.nombre())
                .map(b -> new BranchResponse(b.id(), b.nombre(), b.franchiseId()));
    }

    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en una sucursal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        log.debug("POST /productos - branchId: {}, nombre: {}, stock: {}", request.branchId(), request.nombre(), request.stock());
        return addProductUseCase.execute(request.branchId(), request.nombre(), request.stock())
                .map(p -> new ProductResponse(p.id(), p.nombre(), p.stock(), p.branchId()));
    }

    @Operation(summary = "Actualizar nombre de producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre actualizado", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/productos/{productId}/nombre")
    public Mono<ProductResponse> updateProductName(
            @PathVariable("productId") @Positive(message = "El ID de producto debe ser positivo") Long productId,
            @Valid @RequestBody ProductNameUpdateRequest request) {
        log.debug("PATCH /productos/{}/nombre - nombre: {}", productId, request.nombre());
        return updateProductNameUseCase.execute(productId, request.nombre())
                .map(p -> new ProductResponse(p.id(), p.nombre(), p.stock(), p.branchId()));
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto de una sucursal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Producto o sucursal no encontrados")
    })
    @DeleteMapping("/sucursales/{branchId}/productos/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(
            @PathVariable("branchId") @Positive(message = "El ID de sucursal debe ser positivo") Long branchId,
            @PathVariable("productId") @Positive(message = "El ID de producto debe ser positivo") Long productId) {
        log.debug("DELETE /sucursales/{}/productos/{}", branchId, productId);
        return deleteProductUseCase.execute(productId, branchId);
    }

    @Operation(summary = "Actualizar stock de producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actualizado", content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/productos/{productId}/stock")
    public Mono<ProductResponse> updateProductStock(
            @PathVariable("productId") @Positive(message = "El ID de producto debe ser positivo") Long productId,
            @Valid @RequestBody StockUpdateRequest request) {
        log.debug("PATCH /productos/{}/stock - nuevoStock: {}", productId, request.stock());
        return updateProductStockUseCase.execute(productId, request.stock())
                .map(p -> new ProductResponse(p.id(), p.nombre(), p.stock(), p.branchId()));
    }

    @Operation(summary = "Productos con mayor stock", description = "Obtiene el producto con mayor stock por cada sucursal de una franquicia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos", content = @Content(schema = @Schema(implementation = MaxStockProductsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación"),
            @ApiResponse(responseCode = "404", description = "Franquicia no encontrada")
    })
    @GetMapping("/franquicias/{franchiseId}/productos-mayor-stock")
    public Mono<MaxStockProductsResponse> getMaxStockProductsByFranchise(
            @PathVariable("franchiseId") @Positive(message = "El ID de franquicia debe ser positivo") Long franchiseId) {
        log.debug("GET /franquicias/{}/productos-mayor-stock", franchiseId);
        return getMaxStockProductsUseCase.execute(franchiseId)
                .map(list -> {
                    List<ProductWithBranchResponse> productos = list.stream()
                            .map(p -> new ProductWithBranchResponse(
                                    p.productId(), p.productName(), p.stock(),
                                    p.branchId(), p.branchName()))
                            .toList();
                    return new MaxStockProductsResponse(franchiseId, productos);
                });
    }
}

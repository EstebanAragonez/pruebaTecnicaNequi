package com.nequi.franquicias.config;

import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.FranchiseRepository;
import com.nequi.franquicias.model.port.ProductRepository;
import com.nequi.franquicias.usecase.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("UseCaseConfig")
class UseCaseConfigTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    private UseCaseConfig config;

    @BeforeEach
    void setUp() {
        config = new UseCaseConfig();
    }

    @Test
    @DisplayName("crea AddFranchiseUseCase")
    void creaAddFranchiseUseCase() {
        assertNotNull(config.addFranchiseUseCase(franchiseRepository));
    }

    @Test
    @DisplayName("crea UpdateFranchiseNameUseCase")
    void creaUpdateFranchiseNameUseCase() {
        assertNotNull(config.updateFranchiseNameUseCase(franchiseRepository));
    }

    @Test
    @DisplayName("crea AddBranchUseCase")
    void creaAddBranchUseCase() {
        assertNotNull(config.addBranchUseCase(franchiseRepository, branchRepository));
    }

    @Test
    @DisplayName("crea UpdateBranchNameUseCase")
    void creaUpdateBranchNameUseCase() {
        assertNotNull(config.updateBranchNameUseCase(branchRepository));
    }

    @Test
    @DisplayName("crea AddProductUseCase")
    void creaAddProductUseCase() {
        assertNotNull(config.addProductUseCase(branchRepository, productRepository));
    }

    @Test
    @DisplayName("crea UpdateProductNameUseCase")
    void creaUpdateProductNameUseCase() {
        assertNotNull(config.updateProductNameUseCase(productRepository));
    }

    @Test
    @DisplayName("crea UpdateProductStockUseCase")
    void creaUpdateProductStockUseCase() {
        assertNotNull(config.updateProductStockUseCase(productRepository));
    }

    @Test
    @DisplayName("crea DeleteProductUseCase")
    void creaDeleteProductUseCase() {
        assertNotNull(config.deleteProductUseCase(productRepository, branchRepository));
    }

    @Test
    @DisplayName("crea GetMaxStockProductsByFranchiseUseCase")
    void creaGetMaxStockProductsByFranchiseUseCase() {
        assertNotNull(config.getMaxStockProductsByFranchiseUseCase(
                franchiseRepository, branchRepository, productRepository));
    }
}

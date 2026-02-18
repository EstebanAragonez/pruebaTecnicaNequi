package com.nequi.franquicias.config;

import com.nequi.franquicias.model.port.BranchRepository;
import com.nequi.franquicias.model.port.FranchiseRepository;
import com.nequi.franquicias.model.port.ProductRepository;
import com.nequi.franquicias.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans para los casos de uso.
 * Inyecta las implementaciones concretas de los puertos (driven-adapters).
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public AddFranchiseUseCase addFranchiseUseCase(FranchiseRepository franchiseRepository) {
        return new AddFranchiseUseCase(franchiseRepository);
    }

    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseRepository franchiseRepository) {
        return new UpdateFranchiseNameUseCase(franchiseRepository);
    }

    @Bean
    public AddBranchUseCase addBranchUseCase(FranchiseRepository franchiseRepository, BranchRepository branchRepository) {
        return new AddBranchUseCase(franchiseRepository, branchRepository);
    }

    @Bean
    public UpdateBranchNameUseCase updateBranchNameUseCase(BranchRepository branchRepository) {
        return new UpdateBranchNameUseCase(branchRepository);
    }

    @Bean
    public AddProductUseCase addProductUseCase(BranchRepository branchRepository, ProductRepository productRepository) {
        return new AddProductUseCase(branchRepository, productRepository);
    }

    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(ProductRepository productRepository) {
        return new UpdateProductNameUseCase(productRepository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository productRepository) {
        return new UpdateProductStockUseCase(productRepository);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new DeleteProductUseCase(productRepository, branchRepository);
    }

    @Bean
    public GetMaxStockProductsByFranchiseUseCase getMaxStockProductsByFranchiseUseCase(
            FranchiseRepository franchiseRepository,
            BranchRepository branchRepository,
            ProductRepository productRepository) {
        return new GetMaxStockProductsByFranchiseUseCase(franchiseRepository, branchRepository, productRepository);
    }
}

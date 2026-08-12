package tech.kayys.erp.catalog.application.api;

import tech.kayys.erp.catalog.application.api.command.CreateProductCommand;
import tech.kayys.erp.catalog.application.api.command.UpdateProductCommand;
import tech.kayys.erp.catalog.application.api.command.ActivateProductCommand;
import tech.kayys.erp.catalog.application.api.command.DeactivateProductCommand;
import tech.kayys.erp.catalog.application.api.command.AdjustStockCommand;
import tech.kayys.erp.catalog.domain.identifier.ProductId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for product commands.
 * This is the entry point for all write operations in the catalog.
 */
public interface ProductCommandService {

    /**
     * Creates a new product in the catalog.
     * 
     * @param command The create product command
     * @return The ID of the created product
     */
    CompletionStage<ProductId> createProduct(CreateProductCommand command);

    /**
     * Updates an existing product.
     * 
     * @param command The update product command
     * @return The ID of the updated product
     */
    CompletionStage<ProductId> updateProduct(UpdateProductCommand command);

    /**
     * Activates a product for sale.
     * 
     * @param command The activate product command
     * @return The ID of the activated product
     */
    CompletionStage<ProductId> activateProduct(ActivateProductCommand command);

    /**
     * Deactivates a product.
     * 
     * @param command The deactivate product command
     * @return The ID of the deactivated product
     */
    CompletionStage<ProductId> deactivateProduct(DeactivateProductCommand command);

    /**
     * Adjusts the stock level of a product.
     * 
     * @param command The adjust stock command
     * @return The ID of the product
     */
    CompletionStage<ProductId> adjustStock(AdjustStockCommand command);
}
package tech.kayys.erp.catalog.application.api;

import tech.kayys.erp.catalog.application.api.query.GetProductQuery;
import tech.kayys.erp.catalog.application.api.query.ProductView;
import tech.kayys.erp.catalog.application.api.query.SearchProductsQuery;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for product queries.
 * This is the entry point for all read operations in the catalog.
 */
public interface ProductQueryService {

    /**
     * Retrieves a product by its ID.
     * 
     * @param query The get product query
     * @return The product view, or empty if not found
     */
    CompletionStage<ProductView> getProduct(GetProductQuery query);

    /**
     * Searches for products based on criteria.
     * 
     * @param query The search query
     * @return List of matching product views
     */
    CompletionStage<List<ProductView>> searchProducts(SearchProductsQuery query);

    /**
     * Checks if a product exists.
     * 
     * @param productId The product ID
     * @return True if the product exists
     */
    CompletionStage<Boolean> productExists(ProductId productId);
}
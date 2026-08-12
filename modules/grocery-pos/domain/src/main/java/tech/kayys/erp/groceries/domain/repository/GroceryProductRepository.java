package tech.kayys.erp.groceries.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;
import tech.kayys.erp.groceries.domain.model.GroceryProduct;

import java.util.concurrent.CompletionStage;

/**
 * Repository for GroceryProduct aggregate.
 */
public interface GroceryProductRepository extends Repository<GroceryProductId, GroceryProduct> {

    CompletionStage<GroceryProduct> findByCatalogProductId(java.util.UUID catalogProductId);

    CompletionStage<java.util.List<GroceryProduct>> findExpiringProducts(int daysThreshold);

    CompletionStage<java.util.List<GroceryProduct>> findExpiredProducts();
}

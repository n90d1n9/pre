package tech.kayys.syirkah.groceries.domain.repository;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.groceries.domain.identifier.ProductId;
import tech.kayys.syirkah.groceries.domain.model.Product;

import java.util.concurrent.CompletionStage;

/**
 * Repository for Product aggregate.
 */
public interface ProductRepository extends Repository<Product, ProductId> {

    CompletionStage<Product> findByCatalogProductId(java.util.UUID catalogProductId);

    CompletionStage<java.util.List<Product>> findExpiringProducts(int daysThreshold);

    CompletionStage<java.util.List<Product>> findExpiredProducts();
}

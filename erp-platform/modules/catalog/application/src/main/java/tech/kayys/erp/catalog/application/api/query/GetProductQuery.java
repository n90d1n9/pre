package tech.kayys.erp.catalog.application.api.query;

import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.foundation.application.Query;

/**
 * Query to retrieve a product by its ID.
 */
public record GetProductQuery(
        ProductId productId
) implements Query<ProductView> {

    public GetProductQuery {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
    }
}
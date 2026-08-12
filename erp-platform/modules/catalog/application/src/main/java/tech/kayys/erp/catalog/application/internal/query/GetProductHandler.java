package tech.kayys.erp.catalog.application.internal.query;

import tech.kayys.erp.catalog.application.api.query.GetProductQuery;
import tech.kayys.erp.catalog.application.api.query.ProductView;
import tech.kayys.erp.catalog.domain.repository.ProductRepository;
import tech.kayys.erp.foundation.application.QueryHandler;
import tech.kayys.erp.foundation.application.UseCase;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Internal handler for retrieving a product.
 */
@UseCase("Get a product by ID")
public class GetProductHandler implements QueryHandler<GetProductQuery, ProductView> {

    private final ProductRepository productRepository;

    public GetProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CompletionStage<ProductView> handle(GetProductQuery query) {
        return productRepository.findById(query.productId())
            .thenApply(productOpt -> productOpt
                .map(ProductView::fromDomain)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + query.productId()))
            );
    }
}
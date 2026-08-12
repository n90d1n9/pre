package tech.kayys.erp.catalog.application.internal.command;

import tech.kayys.erp.catalog.application.api.command.CreateProductCommand;
import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.catalog.domain.model.Product;
import tech.kayys.erp.catalog.domain.repository.ProductRepository;
import tech.kayys.erp.catalog.domain.valueobject.Money;
import tech.kayys.erp.catalog.domain.valueobject.ProductStatus;
import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;

import java.util.concurrent.CompletionStage;

/**
 * Internal handler for creating products.
 * This is the implementation of the create product use case.
 */
@UseCase("Create a new product in the catalog")
public class CreateProductHandler implements CommandHandler<CreateProductCommand, ProductId> {

    private final ProductRepository productRepository;

    public CreateProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CompletionStage<ProductId> handle(CreateProductCommand command) {
        // 1. Validate business rules
        if (command.sku() == null || command.sku().isEmpty()) {
            return CompletableFuture.failedFuture(
                new IllegalArgumentException("SKU is required")
            );
        }

        // 2. Check for duplicate SKU
        return productRepository.existsBySku(command.sku())
            .thenCompose(exists -> {
                if (exists) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Product with SKU " + command.sku() + " already exists")
                    );
                }

                // 3. Create the domain aggregate
                Money price = Money.of(command.price(), command.currencyCode());
                Product product = Product.create(
                    command.productId(),
                    command.name(),
                    command.description(),
                    price,
                    command.sku()
                );

                // 4. Save the aggregate
                return productRepository.save(product)
                    .thenApply(saved -> saved.getId());
            });
    }
}
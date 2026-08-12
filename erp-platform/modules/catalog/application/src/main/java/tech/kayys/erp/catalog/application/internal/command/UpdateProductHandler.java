package tech.kayys.erp.catalog.application.internal.command;

import tech.kayys.erp.catalog.application.api.command.UpdateProductCommand;
import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.catalog.domain.model.Product;
import tech.kayys.erp.catalog.domain.repository.ProductRepository;
import tech.kayys.erp.catalog.domain.valueobject.Money;
import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Internal handler for updating products.
 */
@UseCase("Update an existing product")
public class UpdateProductHandler implements CommandHandler<UpdateProductCommand, ProductId> {

    private final ProductRepository productRepository;

    public UpdateProductHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public CompletionStage<ProductId> handle(UpdateProductCommand command) {
        return productRepository.findById(command.productId())
            .thenCompose(productOpt -> {
                if (productOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Product not found: " + command.productId())
                    );
                }

                Product product = productOpt.get();
                
                // Update product details
                // Note: In a real implementation, you'd have update methods on Product
                // For now, this is a simplified version
                
                // Check if price changed
                Money newPrice = Money.of(command.price(), command.currencyCode());
                if (!product.getPrice().equals(newPrice)) {
                    product.changePrice(newPrice);
                }
                
                // Update other fields (in a real implementation, these would be business methods)
                // This demonstrates the pattern
                
                return productRepository.save(product)
                    .thenApply(Product::getId);
            });
    }
}
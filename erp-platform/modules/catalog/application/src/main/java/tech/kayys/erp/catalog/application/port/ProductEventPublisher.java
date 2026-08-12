package tech.kayys.erp.catalog.application.port;

import tech.kayys.erp.catalog.domain.event.ProductCreated;
import tech.kayys.erp.catalog.domain.event.ProductPriceChanged;
import tech.kayys.erp.catalog.domain.model.Product;

import java.util.concurrent.CompletionStage;

/**
 * Port for publishing domain events to other bounded contexts.
 * Implementation is provided by the infrastructure layer.
 */
public interface ProductEventPublisher {

    /**
     * Publishes a ProductCreated event.
     */
    CompletionStage<Void> publishProductCreated(ProductCreated event);

    /**
     * Publishes a ProductPriceChanged event.
     */
    CompletionStage<Void> publishProductPriceChanged(ProductPriceChanged event);

    /**
     * Publishes all events for a product aggregate.
     * This is typically called after saving the aggregate.
     */
    default CompletionStage<Void> publishAllEvents(Product product) {
        return product.getDomainEvents().stream()
            .map(event -> {
                if (event instanceof ProductCreated) {
                    return publishProductCreated((ProductCreated) event);
                } else if (event instanceof ProductPriceChanged) {
                    return publishProductPriceChanged((ProductPriceChanged) event);
                } else {
                    return CompletableFuture.completedStage(null);
                }
            })
            .reduce(CompletableFuture.completedStage(null), 
                (acc, stage) -> acc.thenCompose(v -> stage));
    }
}
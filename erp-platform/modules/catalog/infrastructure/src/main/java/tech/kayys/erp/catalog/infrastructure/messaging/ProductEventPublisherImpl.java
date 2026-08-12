package tech.kayys.erp.catalog.infrastructure.messaging;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import tech.kayys.erp.catalog.application.port.ProductEventPublisher;
import tech.kayys.erp.catalog.domain.event.ProductCreated;
import tech.kayys.erp.catalog.domain.event.ProductPriceChanged;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Implementation of ProductEventPublisher using Kafka.
 * This is an infrastructure adapter.
 */
@ApplicationScoped
public class ProductEventPublisherImpl implements ProductEventPublisher {

    @Inject
    @Channel("product-events")
    Emitter<String> productEventsEmitter;

    @Override
    public CompletionStage<Void> publishProductCreated(ProductCreated event) {
        // Convert event to JSON and publish to Kafka
        String json = toJson(event);
        return Uni.createFrom()
            .completionStage(productEventsEmitter.send(json))
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    @Override
    public CompletionStage<Void> publishProductPriceChanged(ProductPriceChanged event) {
        String json = toJson(event);
        return Uni.createFrom()
            .completionStage(productEventsEmitter.send(json))
            .onItem()
            .transform(v -> null)
            .subscribe()
            .asCompletionStage();
    }

    private String toJson(Object event) {
        // In a real implementation, use Jackson or a JSON library
        // This is a placeholder
        return "{\"event\": \"" + event.getClass().getSimpleName() + "\"}";
    }
}
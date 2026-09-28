package tech.kayys.syirkah.product.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.product.domain.identifier.IdentifierType;
import tech.kayys.syirkah.product.domain.product.ProductId;

import java.time.Instant;
import java.util.UUID;

public record ProductIdentifierRemoved(
        UUID eventId,
        Instant occurredAt,
        ProductId productId,
        IdentifierType identifierType,
        String value
) implements DomainEvent {

    @Override
    public String eventType() {
        return "product.product-identifier-removed";
    }
}
package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionPaused implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;

    public SubscriptionPaused(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionPaused";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
    }

    @Override
    public UUID getEventId() { return eventId; }
    @Override
    public String getEventType() { return eventType; }
    @Override
    public Instant getOccurredAt() { return occurredAt; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getAggregateType() { return aggregateType; }

    @Override
    public String toString() {
        return "SubscriptionPaused{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                '}';
    }
}
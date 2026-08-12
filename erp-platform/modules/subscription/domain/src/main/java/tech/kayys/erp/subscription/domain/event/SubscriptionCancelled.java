package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionCancelled implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String reason;

    public SubscriptionCancelled(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionCancelled";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
        this.reason = subscription.getCancellationReason();
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
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return "SubscriptionCancelled{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                ", reason='" + reason + '\'' +
                '}';
    }
}
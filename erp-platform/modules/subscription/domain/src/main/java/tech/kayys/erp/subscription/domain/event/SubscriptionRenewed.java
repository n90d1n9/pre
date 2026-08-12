package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionRenewed implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String nextBillingDate;

    public SubscriptionRenewed(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionRenewed";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
        this.nextBillingDate = subscription.getNextBillingDate().toString();
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
    public String getNextBillingDate() { return nextBillingDate; }

    @Override
    public String toString() {
        return "SubscriptionRenewed{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                ", nextBillingDate='" + nextBillingDate + '\'' +
                '}';
    }
}
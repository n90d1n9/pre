package tech.kayys.erp.subscription.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;
import java.util.UUID;

public class SubscriptionCreated implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String customerId;
    private final String planId;
    private final String monthlyFee;
    private final String currency;

    public SubscriptionCreated(Subscription subscription) {
        this.eventId = UUID.randomUUID();
        this.eventType = "SubscriptionCreated";
        this.occurredAt = Instant.now();
        this.aggregateId = subscription.getId().toString();
        this.aggregateType = "Subscription";
        this.customerId = subscription.getCustomerId().toString();
        this.planId = subscription.getPlanId().toString();
        this.monthlyFee = subscription.getMonthlyFee().getAmount().toPlainString();
        this.currency = subscription.getMonthlyFee().getCurrency().getCurrencyCode();
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
    public String getCustomerId() { return customerId; }
    public String getPlanId() { return planId; }
    public String getMonthlyFee() { return monthlyFee; }
    public String getCurrency() { return currency; }

    @Override
    public String toString() {
        return "SubscriptionCreated{" +
                "eventId=" + eventId +
                ", subscriptionId=" + aggregateId +
                ", customerId='" + customerId + '\'' +
                ", planId='" + planId + '\'' +
                '}';
    }
}
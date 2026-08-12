package tech.kayys.erp.sales.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.model.Order;

import java.time.Instant;
import java.util.UUID;

public class OrderCreated implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String customerId;
    private final int itemCount;

    public OrderCreated(Order order) {
        this.eventId = UUID.randomUUID();
        this.eventType = "OrderCreated";
        this.occurredAt = Instant.now();
        this.aggregateId = order.getId().toString();
        this.aggregateType = "Order";
        this.customerId = order.getCustomerId().toString();
        this.itemCount = order.getItems().size();
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getAggregateId() {
        return aggregateId;
    }

    @Override
    public String getAggregateType() {
        return aggregateType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getItemCount() {
        return itemCount;
    }

    @Override
    public String toString() {
        return "OrderCreated{" +
                "eventId=" + eventId +
                ", orderId=" + aggregateId +
                ", customerId='" + customerId + '\'' +
                '}';
    }
}
package tech.kayys.erp.sales.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.model.Order;

import java.time.Instant;
import java.util.UUID;

public class OrderSubmitted implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String grandTotal;
    private final String currency;

    public OrderSubmitted(Order order) {
        this.eventId = UUID.randomUUID();
        this.eventType = "OrderSubmitted";
        this.occurredAt = Instant.now();
        this.aggregateId = order.getId().toString();
        this.aggregateType = "Order";
        this.grandTotal = order.getGrandTotal().getAmount().toPlainString();
        this.currency = order.getGrandTotal().getCurrency().getCurrencyCode();
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

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "OrderSubmitted{" +
                "eventId=" + eventId +
                ", orderId=" + aggregateId +
                ", grandTotal=" + grandTotal +
                '}';
    }
}
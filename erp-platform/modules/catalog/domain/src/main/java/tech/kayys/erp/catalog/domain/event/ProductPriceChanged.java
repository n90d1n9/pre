package tech.kayys.erp.catalog.domain.event;

import tech.kayys.erp.catalog.domain.model.Product;
import tech.kayys.erp.catalog.domain.valueobject.Money;
import tech.kayys.erp.foundation.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class ProductPriceChanged implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String oldPrice;
    private final String newPrice;
    private final String currency;

    public ProductPriceChanged(Product product, Money oldPrice, Money newPrice) {
        this.eventId = UUID.randomUUID();
        this.eventType = "ProductPriceChanged";
        this.occurredAt = Instant.now();
        this.aggregateId = product.getId().toString();
        this.aggregateType = "Product";
        this.oldPrice = oldPrice.getAmount().toPlainString();
        this.newPrice = newPrice.getAmount().toPlainString();
        this.currency = newPrice.getCurrency().getCurrencyCode();
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

    public String getOldPrice() {
        return oldPrice;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "ProductPriceChanged{" +
                "eventId=" + eventId +
                ", oldPrice=" + oldPrice +
                ", newPrice=" + newPrice +
                '}';
    }
}

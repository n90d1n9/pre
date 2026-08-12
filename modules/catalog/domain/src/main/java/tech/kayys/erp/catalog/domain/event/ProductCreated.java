package tech.kayys.erp.catalog.domain.event;

import tech.kayys.erp.catalog.domain.model.Product;
import tech.kayys.erp.foundation.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class ProductCreated implements DomainEvent {

    private static final long serialVersionUID = 1L;

    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String productName;
    private final String sku;
    private final String price;
    private final String currency;

    public ProductCreated(Product product) {
        this.eventId = UUID.randomUUID();
        this.eventType = "ProductCreated";
        this.occurredAt = Instant.now();
        this.aggregateId = product.getId().toString();
        this.aggregateType = "Product";
        this.productName = product.getName();
        this.sku = product.getSku();
        this.price = product.getPrice().getAmount().toPlainString();
        this.currency = product.getPrice().getCurrency().getCurrencyCode();
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

    public String getProductName() {
        return productName;
    }

    public String getSku() {
        return sku;
    }

    public String getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "ProductCreated{" +
                "eventId=" + eventId +
                ", productName='" + productName + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}

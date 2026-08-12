package tech.kayys.erp.purchasing.domain.event;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;

import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderReceived implements DomainEvent {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final String aggregateType;
    private final String poNumber;
    private final String vendorId;
    private final int totalItemsReceived;
    private final boolean fullyReceived;

    public PurchaseOrderReceived(PurchaseOrder po) {
        this.eventId = UUID.randomUUID();
        this.eventType = "PurchaseOrderReceived";
        this.occurredAt = Instant.now();
        this.aggregateId = po.getId().toString();
        this.aggregateType = "PurchaseOrder";
        this.poNumber = po.getPoNumber();
        this.vendorId = po.getVendorId().toString();
        this.totalItemsReceived = po.getTotalItemsReceived();
        this.fullyReceived = po.isFullyReceived();
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
    public String getPoNumber() { return poNumber; }
    public String getVendorId() { return vendorId; }
    public int getTotalItemsReceived() { return totalItemsReceived; }
    public boolean isFullyReceived() { return fullyReceived; }

    @Override
    public String toString() {
        return "PurchaseOrderReceived{" +
                "eventId=" + eventId +
                ", poNumber='" + poNumber + '\'' +
                ", fullyReceived=" + fullyReceived +
                '}';
    }
}
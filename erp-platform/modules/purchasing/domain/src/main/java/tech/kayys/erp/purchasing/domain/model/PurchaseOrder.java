package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.event.PurchaseOrderCreated;
import tech.kayys.erp.purchasing.domain.event.PurchaseOrderReceived;
import tech.kayys.erp.purchasing.domain.event.PurchaseOrderSubmitted;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.valueobject.Money;
import tech.kayys.erp.purchasing.domain.valueobject.PurchaseOrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Purchase Order aggregate root.
 * Represents an order placed with a vendor for goods or services.
 */
public final class PurchaseOrder extends AggregateRoot<PurchaseOrderId> {
    
    private static final long serialVersionUID = 1L;
    
    private String poNumber;
    private VendorId vendorId;
    private String vendorName;
    private List<PurchaseOrderItem> items;
    private Money subtotal;
    private Money taxTotal;
    private Money shippingCost;
    private Money discountTotal;
    private Money grandTotal;
    private PurchaseOrderStatus status;
    private Instant orderDate;
    private Instant requiredDate;
    private Instant receivedDate;
    private String shippingAddress;
    private String billingAddress;
    private String paymentTerms;
    private String shippingTerms;
    private String currencyCode;
    private String notes;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private String vendorReference;
    private String trackingNumber;
    private String deliveryMethod;
    private int totalItemsReceived;

    private PurchaseOrder(PurchaseOrderId id) {
        super(id);
        this.items = new ArrayList<>();
        this.status = PurchaseOrderStatus.DRAFT;
        this.orderDate = Instant.now();
        this.totalItemsReceived = 0;
        this.shippingCost = Money.zero("USD");
        this.discountTotal = Money.zero("USD");
        this.taxTotal = Money.zero("USD");
    }

    private PurchaseOrder() {
        super();
    }

    /**
     * Factory method to create a new purchase order.
     */
    public static PurchaseOrder create(
            PurchaseOrderId id,
            String poNumber,
            VendorId vendorId,
            String vendorName,
            Instant requiredDate,
            String currencyCode) {
        PurchaseOrder po = new PurchaseOrder(id);
        po.poNumber = poNumber;
        po.vendorId = vendorId;
        po.vendorName = vendorName;
        po.requiredDate = requiredDate;
        po.currencyCode = currencyCode;
        po.status = PurchaseOrderStatus.DRAFT;
        
        po.registerEvent(new PurchaseOrderCreated(po));
        return po;
    }

    /**
     * Adds an item to the purchase order.
     */
    public void addItem(PurchaseOrderItem item) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        items.add(item);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an item from the purchase order.
     */
    public void removeItem(int index) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("Invalid item index");
        }
        items.remove(index);
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the purchase order to the vendor.
     */
    public void submit() {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot submit PO in status: " + status);
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("PO must have at least one item");
        }
        
        this.status = PurchaseOrderStatus.SUBMITTED;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new PurchaseOrderSubmitted(this));
    }

    /**
     * Acknowledges the purchase order from the vendor.
     */
    public void acknowledge(String vendorReference) {
        if (status != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot acknowledge PO in status: " + status);
        }
        
        this.status = PurchaseOrderStatus.ACKNOWLEDGED;
        this.vendorReference = vendorReference;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records that the purchase order is in transit.
     */
    public void markInTransit(String trackingNumber) {
        if (status != PurchaseOrderStatus.ACKNOWLEDGED) {
            throw new IllegalStateException("Cannot mark in transit from status: " + status);
        }
        
        this.status = PurchaseOrderStatus.IN_TRANSIT;
        this.trackingNumber = trackingNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records receipt of items.
     */
    public void receiveItems(List<ReceivedItem> receivedItems) {
        if (!status.isReceivable()) {
            throw new IllegalStateException("Cannot receive items in status: " + status);
        }

        for (ReceivedItem received : receivedItems) {
            PurchaseOrderItem item = items.get(received.itemIndex);
            if (item == null) {
                throw new IllegalArgumentException("Item not found at index: " + received.itemIndex);
            }
            
            int receivedQty = received.quantityReceived;
            if (receivedQty > item.getRemainingQuantity()) {
                throw new IllegalArgumentException(
                    "Received quantity exceeds remaining: " + receivedQty + " > " + item.getRemainingQuantity()
                );
            }
            
            item.receive(receivedQty);
            totalItemsReceived += receivedQty;
        }

        // Check if all items are fully received
        boolean allReceived = items.stream().allMatch(poItem -> poItem.isFullyReceived());
        if (allReceived) {
            this.status = PurchaseOrderStatus.RECEIVED;
            this.receivedDate = Instant.now();
        } else {
            this.status = PurchaseOrderStatus.PARTIALLY_RECEIVED;
        }

        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new PurchaseOrderReceived(this));
    }

    /**
     * Completes the purchase order.
     */
    public void complete() {
        if (status != PurchaseOrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot complete PO in status: " + status);
        }
        if (!items.stream().allMatch(PurchaseOrderItem::isFullyReceived)) {
            throw new IllegalStateException("Not all items have been received");
        }
        
        this.status = PurchaseOrderStatus.COMPLETED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the purchase order.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("PO is already finalized");
        }
        
        this.status = PurchaseOrderStatus.CANCELLED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the purchase order from the vendor.
     */
    public void reject(String reason) {
        if (status != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot reject PO in status: " + status);
        }
        
        this.status = PurchaseOrderStatus.REJECTED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Places the purchase order on hold.
     */
    public void putOnHold(String reason) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot put PO on hold in status: " + status);
        }
        
        this.status = PurchaseOrderStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void recalculateTotals() {
        // Calculate subtotal
        Money newSubtotal = items.stream()
            .map(PurchaseOrderItem::getLineTotal)
            .reduce(Money.zero(currencyCode), Money::add);
        
        // Calculate tax (simplified - 10%)
        Money newTaxTotal = newSubtotal.percentage(10);
        
        // Calculate grand total
        Money newGrandTotal = newSubtotal
            .add(newTaxTotal)
            .add(shippingCost)
            .subtract(discountTotal);
        
        this.subtotal = newSubtotal;
        this.taxTotal = newTaxTotal;
        this.grandTotal = newGrandTotal;
    }

    // Getters
    public String getPoNumber() { return poNumber; }
    public VendorId getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public List<PurchaseOrderItem> getItems() { return Collections.unmodifiableList(items); }
    public Money getSubtotal() { return subtotal; }
    public Money getTaxTotal() { return taxTotal; }
    public Money getShippingCost() { return shippingCost; }
    public Money getDiscountTotal() { return discountTotal; }
    public Money getGrandTotal() { return grandTotal; }
    public PurchaseOrderStatus getStatus() { return status; }
    public Instant getOrderDate() { return orderDate; }
    public Instant getRequiredDate() { return requiredDate; }
    public Instant getReceivedDate() { return receivedDate; }
    public String getShippingAddress() { return shippingAddress; }
    public String getBillingAddress() { return billingAddress; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getShippingTerms() { return shippingTerms; }
    public String getCurrencyCode() { return currencyCode; }
    public String getNotes() { return notes; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getVendorReference() { return vendorReference; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public int getTotalItemsReceived() { return totalItemsReceived; }

    public void setShippingAddress(String shippingAddress) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.shippingAddress = shippingAddress;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBillingAddress(String billingAddress) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.billingAddress = billingAddress;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentTerms(String paymentTerms) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.paymentTerms = paymentTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingTerms(String shippingTerms) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.shippingTerms = shippingTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingCost(Money shippingCost) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.shippingCost = shippingCost;
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDiscountTotal(Money discountTotal) {
        if (status != PurchaseOrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify PO in status: " + status);
        }
        this.discountTotal = discountTotal;
        recalculateTotals();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void approve(String approvedBy) {
        if (status != PurchaseOrderStatus.DRAFT && status != PurchaseOrderStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot approve PO in status: " + status);
        }
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if all items have been received.
     */
    public boolean isFullyReceived() {
        return items.stream().allMatch(PurchaseOrderItem::isFullyReceived);
    }

    /**
     * Gets the percentage of items received.
     */
    public double getCompletionPercentage() {
        if (items.isEmpty()) {
            return 0.0;
        }
        int totalOrdered = items.stream()
            .mapToInt(PurchaseOrderItem::getQuantity)
            .sum();
        return (double) totalItemsReceived / totalOrdered * 100.0;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + getId() +
                ", poNumber='" + poNumber + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", status=" + status +
                ", total=" + grandTotal +
                '}';
    }

    /**
     * Purchase Order Item.
     */
    public static final class PurchaseOrderItem implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final UUID productId;
        private final String productName;
        private final String sku;
        private final int quantity;
        private final Money unitPrice;
        private final Money lineTotal;
        private final String uom;
        private int quantityReceived;

        public PurchaseOrderItem(
                UUID productId,
                String productName,
                String sku,
                int quantity,
                Money unitPrice,
                String uom) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.uom = uom;
            this.lineTotal = unitPrice.multiply(quantity);
            this.quantityReceived = 0;
            validate();
        }

        @Override
        public void validate() {
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.isNegative()) {
                throw new IllegalArgumentException("Unit price must be positive");
            }
        }

        public UUID getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public Money getUnitPrice() { return unitPrice; }
        public Money getLineTotal() { return lineTotal; }
        public String getUom() { return uom; }
        public int getQuantityReceived() { return quantityReceived; }
        public int getRemainingQuantity() { return quantity - quantityReceived; }
        public boolean isFullyReceived() { return quantityReceived >= quantity; }

        public void receive(int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Received quantity must be positive");
            }
            if (quantityReceived + quantity > this.quantity) {
                throw new IllegalArgumentException("Cannot receive more than ordered: " + 
                    (quantityReceived + quantity) + " > " + this.quantity);
            }
            this.quantityReceived += quantity;
        }

        @Override
        public String toString() {
            return "PurchaseOrderItem{" +
                    "productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", received=" + quantityReceived +
                    ", lineTotal=" + lineTotal +
                    '}';
        }
    }

    /**
     * Received item record.
     */
    public record ReceivedItem(
            int itemIndex,
            int quantityReceived,
            String notes
    ) {
        public ReceivedItem {
            if (quantityReceived <= 0) {
                throw new IllegalArgumentException("Received quantity must be positive");
            }
        }
    }
}
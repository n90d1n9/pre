package tech.kayys.erp.inventory.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.inventory.domain.identifier.InventoryTransactionId;
import tech.kayys.erp.inventory.domain.identifier.ProductId;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;
import tech.kayys.erp.inventory.domain.valueobject.TransactionType;

import java.time.Instant;

/**
 * Inventory transaction aggregate root.
 * Records every movement of inventory.
 */
public final class InventoryTransaction extends AggregateRoot<InventoryTransactionId> {
    
    private static final long serialVersionUID = 1L;
    
    private ProductId productId;
    private WarehouseId warehouseId;
    private String transactionNumber;
    private TransactionType transactionType;
    private int quantity;
    private int quantityBefore;
    private int quantityAfter;
    private String sourceReference;
    private String sourceType;
    private String destinationReference;
    private String notes;
    private String performedBy;
    private Instant transactionDate;
    private String currencyCode;
    private double unitCost;
    private double totalCost;
    private boolean reversed;
    private String reversedBy;
    private Instant reversedAt;

    private InventoryTransaction(InventoryTransactionId id) {
        super(id);
        this.transactionDate = Instant.now();
        this.reversed = false;
    }

    private InventoryTransaction() {
        super();
    }

    /**
     * Factory method to create a new inventory transaction.
     */
    public static InventoryTransaction create(
            InventoryTransactionId id,
            ProductId productId,
            WarehouseId warehouseId,
            TransactionType transactionType,
            int quantity,
            int quantityBefore,
            String performedBy,
            String currencyCode) {
        InventoryTransaction transaction = new InventoryTransaction(id);
        transaction.productId = productId;
        transaction.warehouseId = warehouseId;
        transaction.transactionType = transactionType;
        transaction.quantity = quantity;
        transaction.quantityBefore = quantityBefore;
        transaction.performedBy = performedBy;
        transaction.currencyCode = currencyCode;
        transaction.transactionNumber = generateTransactionNumber(transactionType);
        transaction.quantityAfter = quantityBefore + quantity;
        return transaction;
    }

    private static String generateTransactionNumber(TransactionType type) {
        return type.name().substring(0, 3) + "-" + System.currentTimeMillis();
    }

    /**
     * Reverses the transaction.
     */
    public void reverse(String reversedBy, String reason) {
        if (reversed) {
            throw new IllegalStateException("Transaction already reversed");
        }
        this.reversed = true;
        this.reversedBy = reversedBy;
        this.reversedAt = Instant.now();
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets source reference.
     */
    public void setSource(String sourceType, String sourceReference) {
        this.sourceType = sourceType;
        this.sourceReference = sourceReference;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets destination reference.
     */
    public void setDestination(String destinationReference) {
        this.destinationReference = destinationReference;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public ProductId getProductId() { return productId; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public String getTransactionNumber() { return transactionNumber; }
    public TransactionType getTransactionType() { return transactionType; }
    public int getQuantity() { return quantity; }
    public int getQuantityBefore() { return quantityBefore; }
    public int getQuantityAfter() { return quantityAfter; }
    public String getSourceReference() { return sourceReference; }
    public String getSourceType() { return sourceType; }
    public String getDestinationReference() { return destinationReference; }
    public String getNotes() { return notes; }
    public String getPerformedBy() { return performedBy; }
    public Instant getTransactionDate() { return transactionDate; }
    public String getCurrencyCode() { return currencyCode; }
    public double getUnitCost() { return unitCost; }
    public double getTotalCost() { return totalCost; }
    public boolean isReversed() { return reversed; }
    public String getReversedBy() { return reversedBy; }
    public Instant getReversedAt() { return reversedAt; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
        this.totalCost = unitCost * quantity;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "InventoryTransaction{" +
                "id=" + getId() +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", productId=" + productId +
                ", type=" + transactionType +
                ", quantity=" + quantity +
                ", reversed=" + reversed +
                '}';
    }
}
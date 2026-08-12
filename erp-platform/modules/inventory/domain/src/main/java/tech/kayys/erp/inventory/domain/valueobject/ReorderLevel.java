package tech.kayys.erp.inventory.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Reorder level value object.
 */
public final class ReorderLevel implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final int reorderPoint;
    private final int reorderQuantity;
    private final int maximumStock;
    private final int minimumStock;
    private final boolean autoReorder;

    public ReorderLevel(
            int reorderPoint,
            int reorderQuantity,
            int maximumStock,
            int minimumStock,
            boolean autoReorder) {
        this.reorderPoint = reorderPoint;
        this.reorderQuantity = reorderQuantity;
        this.maximumStock = maximumStock;
        this.minimumStock = minimumStock;
        this.autoReorder = autoReorder;
        validate();
    }

    @Override
    public void validate() {
        if (reorderPoint < 0) {
            throw new IllegalArgumentException("Reorder point cannot be negative");
        }
        if (reorderQuantity <= 0) {
            throw new IllegalArgumentException("Reorder quantity must be positive");
        }
        if (maximumStock <= 0) {
            throw new IllegalArgumentException("Maximum stock must be positive");
        }
        if (minimumStock < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }
        if (minimumStock > reorderPoint) {
            throw new IllegalArgumentException("Minimum stock must be less than or equal to reorder point");
        }
    }

    public int getReorderPoint() { return reorderPoint; }
    public int getReorderQuantity() { return reorderQuantity; }
    public int getMaximumStock() { return maximumStock; }
    public int getMinimumStock() { return minimumStock; }
    public boolean isAutoReorder() { return autoReorder; }

    public boolean needsReorder(int currentStock) {
        return currentStock <= reorderPoint;
    }

    public int getReorderAmount(int currentStock) {
        if (currentStock >= reorderPoint) {
            return 0;
        }
        return Math.min(reorderQuantity, maximumStock - currentStock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReorderLevel that = (ReorderLevel) o;
        return reorderPoint == that.reorderPoint &&
               reorderQuantity == that.reorderQuantity &&
               maximumStock == that.maximumStock &&
               minimumStock == that.minimumStock &&
               autoReorder == that.autoReorder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reorderPoint, reorderQuantity, maximumStock, minimumStock, autoReorder);
    }

    @Override
    public String toString() {
        return "ReorderLevel{" +
                "reorderPoint=" + reorderPoint +
                ", reorderQuantity=" + reorderQuantity +
                ", maxStock=" + maximumStock +
                ", minStock=" + minimumStock +
                '}';
    }

    public static ReorderLevel of(int reorderPoint, int reorderQuantity, int maximumStock) {
        return new ReorderLevel(reorderPoint, reorderQuantity, maximumStock, 0, false);
    }

    public static ReorderLevel of(int reorderPoint, int reorderQuantity, int maximumStock, int minimumStock) {
        return new ReorderLevel(reorderPoint, reorderQuantity, maximumStock, minimumStock, false);
    }
}
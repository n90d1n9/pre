package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Types of inventory movements.
 */
public enum MovementType {
    TRANSFER("Transfer - between warehouses"),
    ADJUSTMENT("Adjustment - correction"),
    SCRAP("Scrap - disposal"),
    SAMPLE("Sample - taken for sample"),
    DAMAGE("Damage - moved to damaged goods"),
    RECALL("Recall - recalled from customers"),
    REPAIR("Repair - sent for repair"),
    LOAN("Loan - loaned out"),
    RETURN("Return - returned from customer");

    private final String description;

    MovementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
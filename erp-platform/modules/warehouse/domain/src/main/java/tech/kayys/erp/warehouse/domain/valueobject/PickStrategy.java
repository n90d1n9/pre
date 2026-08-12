package tech.kayys.erp.warehouse.domain.valueobject;

/**
 * Picking strategies.
 */
public enum PickStrategy {
    FIFO("First In, First Out"),
    LIFO("Last In, First Out"),
    FEFO("First Expired, First Out"),
    ZONE("Zone Picking"),
    BATCH("Batch Picking"),
    WAVE("Wave Picking"),
    CLUSTER("Cluster Picking"),
    OPTIMIZED("Optimized Route");

    private final String description;

    PickStrategy(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAutomated() {
        return this == OPTIMIZED || this == WAVE;
    }

    public boolean requiresSorting() {
        return this == ZONE || this == CLUSTER;
    }
}
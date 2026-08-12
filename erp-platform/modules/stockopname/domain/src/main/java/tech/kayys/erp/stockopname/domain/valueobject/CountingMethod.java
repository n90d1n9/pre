package tech.kayys.erp.stockopname.domain.valueobject;

/**
 * Methods for physical counting.
 */
public enum CountingMethod {
    PERIODIC("Periodic - full physical count"),
    CYCLE("Cycle Counting - ongoing counts"),
    SPOT("Spot Check - random verification"),
    BLIND("Blind Count - count without knowing system quantity"),
    TWO_STAGE("Two-Stage - first blind, then informed");

    private final String description;

    CountingMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
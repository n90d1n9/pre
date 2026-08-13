package tech.kayys.erp.integration.domain.valueobject;

/**
 * Status of an integration.
 */
public enum IntegrationStatus {
    ACTIVE("Active - integration is working"),
    INACTIVE("Inactive - integration is disabled"),
    ERROR("Error - integration has errors"),
    DEGRADED("Degraded - partial functionality"),
    MAINTENANCE("Maintenance - under maintenance"),
    PENDING("Pending - awaiting activation"),
    DISCONNECTED("Disconnected - connection lost");

    private final String description;

    IntegrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }

    public boolean isAvailable() {
        return this == ACTIVE || this == DEGRADED;
    }
}

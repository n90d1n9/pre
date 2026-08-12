package tech.kayys.erp.purchasing.domain.valueobject;

/**
 * Status of a vendor contract.
 */
public enum ContractStatus {
    DRAFT("Draft - being negotiated"),
    PENDING_APPROVAL("Pending Approval - awaiting internal approval"),
    ACTIVE("Active - contract in effect"),
    SUSPENDED("Suspended - temporarily inactive"),
    EXPIRED("Expired - contract period ended"),
    TERMINATED("Terminated - ended early"),
    UNDER_RENEWAL("Under Renewal - being renewed");

    private final String description;

    ContractStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PENDING_APPROVAL;
    }

    public boolean isTerminal() {
        return this == EXPIRED || this == TERMINATED;
    }

    public boolean canTransitionTo(ContractStatus target) {
        return switch (this) {
            case DRAFT -> target == PENDING_APPROVAL || target == TERMINATED;
            case PENDING_APPROVAL -> target == ACTIVE || target == DRAFT || target == TERMINATED;
            case ACTIVE -> target == SUSPENDED || target == EXPIRED || target == TERMINATED || target == UNDER_RENEWAL;
            case SUSPENDED -> target == ACTIVE || target == EXPIRED || target == TERMINATED;
            case UNDER_RENEWAL -> target == ACTIVE || target == EXPIRED || target == TERMINATED;
            case EXPIRED, TERMINATED -> false;
        };
    }
}
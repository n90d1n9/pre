package tech.kayys.erp.document.domain.valueobject;

/**
 * Status of a document.
 */
public enum DocumentStatus {
    DRAFT("Draft - being created"),
    PENDING_APPROVAL("Pending Approval - awaiting review"),
    APPROVED("Approved - reviewed and accepted"),
    REJECTED("Rejected - not accepted"),
    PUBLISHED("Published - final version"),
    ARCHIVED("Archived - historical"),
    EXPIRED("Expired - no longer valid"),
    DELETED("Deleted - removed");

    private final String description;

    DocumentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == DRAFT || this == PENDING_APPROVAL || 
               this == APPROVED || this == PUBLISHED;
    }

    public boolean isFinal() {
        return this == APPROVED || this == PUBLISHED || 
               this == REJECTED || this == ARCHIVED || this == EXPIRED;
    }

    public boolean canTransitionTo(DocumentStatus target) {
        return switch (this) {
            case DRAFT -> target == PENDING_APPROVAL || target == ARCHIVED || target == DELETED;
            case PENDING_APPROVAL -> target == APPROVED || target == REJECTED;
            case APPROVED -> target == PUBLISHED || target == ARCHIVED || target == EXPIRED;
            case PUBLISHED -> target == ARCHIVED || target == EXPIRED || target == DRAFT;
            case REJECTED, ARCHIVED, EXPIRED, DELETED -> false;
        };
    }
}
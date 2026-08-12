package tech.kayys.erp.crm.domain.valueobject;

/**
 * Priority levels for support tickets.
 */
public enum TicketPriority {
    CRITICAL("Critical - system down"),
    HIGH("High - major issue"),
    MEDIUM("Medium - moderate issue"),
    LOW("Low - minor issue"),
    TRIVIAL("Trivial - cosmetic issue");

    private final String description;

    TicketPriority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverity() {
        return switch (this) {
            case CRITICAL -> 1;
            case HIGH -> 2;
            case MEDIUM -> 3;
            case LOW -> 4;
            case TRIVIAL -> 5;
        };
    }

    public String getSlaResponseHours() {
        return switch (this) {
            case CRITICAL -> "1 hour";
            case HIGH -> "4 hours";
            case MEDIUM -> "8 hours";
            case LOW -> "24 hours";
            case TRIVIAL -> "48 hours";
        };
    }
}
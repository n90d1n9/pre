package tech.kayys.erp.integration.domain.valueobject;

/**
 * Status of an integration message.
 */
public enum MessageStatus {
    PENDING("Pending - waiting to be sent"),
    PROCESSING("Processing - being processed"),
    SENT("Sent - message transmitted"),
    DELIVERED("Delivered - successfully delivered"),
    FAILED("Failed - delivery failed"),
    RETRY("Retry - attempting retry"),
    EXPIRED("Expired - message expired"),
    REJECTED("Rejected - receiver rejected");

    private final String description;

    MessageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinal() {
        return this == DELIVERED || this == FAILED || this == EXPIRED || this == REJECTED;
    }

    public boolean isSuccessful() {
        return this == SENT || this == DELIVERED;
    }
}
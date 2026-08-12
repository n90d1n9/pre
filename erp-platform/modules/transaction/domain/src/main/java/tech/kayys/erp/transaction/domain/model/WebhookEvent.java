package tech.kayys.erp.transaction.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.transaction.domain.identifier.WebhookEventId;

import java.time.Instant;

/**
 * Webhook event aggregate root.
 * Manages outbound webhook notifications.
 */
public final class WebhookEvent extends AggregateRoot<WebhookEventId> {
    
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String eventType; // TRANSACTION_CREATED, PAYMENT_SUCCEEDED, etc.
    private String payload;
    private String endpointUrl;
    private WebhookStatus status;
    private int retryCount;
    private int maxRetries;
    private String responseStatus;
    private String responseBody;
    private String errorMessage;
    private Instant sentAt;
    private Instant deliveredAt;
    private boolean delivered;

    private WebhookEvent(WebhookEventId id) {
        super(id);
        this.status = WebhookStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = 3;
        this.delivered = false;
    }

    private WebhookEvent() {
        super();
    }

    /**
     * Factory method to create a new webhook event.
     */
    public static WebhookEvent create(
            WebhookEventId id,
            String transactionId,
            String eventType,
            String payload,
            String endpointUrl) {
        WebhookEvent event = new WebhookEvent(id);
        event.transactionId = transactionId;
        event.eventType = eventType;
        event.payload = payload;
        event.endpointUrl = endpointUrl;
        return event;
    }

    /**
     * Marks the webhook as sent.
     */
    public void markSent() {
        this.status = WebhookStatus.SENT;
        this.sentAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the webhook as delivered.
     */
    public void markDelivered(String responseStatus, String responseBody) {
        this.status = WebhookStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.delivered = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the webhook as failed.
     */
    public void markFailed(String errorMessage) {
        this.retryCount++;
        this.status = WebhookStatus.FAILED;
        this.errorMessage = errorMessage;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reschedules the webhook for retry.
     */
    public void retry() {
        if (retryCount >= maxRetries) {
            this.status = WebhookStatus.EXPIRED;
            setUpdatedAt(Instant.now());
            incrementVersion();
            return;
        }
        this.status = WebhookStatus.PENDING;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the webhook can be retried.
     */
    public boolean canRetry() {
        return retryCount < maxRetries && status != WebhookStatus.DELIVERED;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public String getEndpointUrl() { return endpointUrl; }
    public WebhookStatus getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
    public int getMaxRetries() { return maxRetries; }
    public String getResponseStatus() { return responseStatus; }
    public String getResponseBody() { return responseBody; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getSentAt() { return sentAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public boolean isDelivered() { return delivered; }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "WebhookEvent{" +
                "id=" + getId() +
                ", transactionId='" + transactionId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Webhook status enum.
     */
    public enum WebhookStatus {
        PENDING("Pending - Awaiting delivery"),
        SENT("Sent - Webhook dispatched"),
        DELIVERED("Delivered - Successfully processed"),
        FAILED("Failed - Delivery failed"),
        EXPIRED("Expired - Max retries exceeded");

        private final String description;

        WebhookStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
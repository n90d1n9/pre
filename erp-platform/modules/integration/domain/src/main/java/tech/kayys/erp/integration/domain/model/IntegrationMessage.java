package tech.kayys.erp.integration.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.integration.domain.identifier.IntegrationId;
import tech.kayys.erp.integration.domain.identifier.MessageId;
import tech.kayys.erp.integration.domain.valueobject.MessageStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Integration message aggregate root.
 * Represents a message sent or received through an integration.
 */
public final class IntegrationMessage extends AggregateRoot<MessageId> {
    
    private static final long serialVersionUID = 1L;
    
    private IntegrationId integrationId;
    private String messageId;
    private String correlationId;
    private String direction; // INBOUND, OUTBOUND
    private MessageStatus status;
    private String payload;
    private String headers;
    private String endpoint;
    private String method;
    private int httpStatus;
    private int retryCount;
    private Instant sentAt;
    private Instant deliveredAt;
    private String error;
    private String notes;
    private boolean processed;

    private IntegrationMessage(MessageId id) {
        super(id);
        this.status = MessageStatus.PENDING;
        this.retryCount = 0;
        this.processed = false;
    }

    private IntegrationMessage() {
        super();
    }

    /**
     * Factory method to create a new integration message.
     */
    public static IntegrationMessage create(
            MessageId id,
            IntegrationId integrationId,
            String direction,
            String endpoint,
            String method,
            String payload) {
        IntegrationMessage message = new IntegrationMessage(id);
        message.integrationId = integrationId;
        message.direction = direction;
        message.endpoint = endpoint;
        message.method = method;
        message.payload = payload;
        message.messageId = generateMessageId(integrationId);
        return message;
    }

    private static String generateMessageId(IntegrationId integrationId) {
        return "MSG-" + integrationId.toString().substring(0, 8) + "-" + System.currentTimeMillis();
    }

    /**
     * Sends the message.
     */
    public void send() {
        if (status != MessageStatus.PENDING && status != MessageStatus.RETRY) {
            throw new IllegalStateException("Cannot send message in status: " + status);
        }
        this.status = MessageStatus.PROCESSING;
        this.sentAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as delivered.
     */
    public void markDelivered(int httpStatus) {
        this.status = MessageStatus.DELIVERED;
        this.httpStatus = httpStatus;
        this.deliveredAt = Instant.now();
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as failed.
     */
    public void markFailed(String error) {
        this.status = MessageStatus.FAILED;
        this.error = error;
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Retries the message.
     */
    public void retry() {
        if (status == MessageStatus.DELIVERED) {
            throw new IllegalStateException("Cannot retry delivered message");
        }
        this.retryCount++;
        this.status = MessageStatus.RETRY;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as expired.
     */
    public void expire() {
        this.status = MessageStatus.EXPIRED;
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as rejected.
     */
    public void reject(String error) {
        this.status = MessageStatus.REJECTED;
        this.error = error;
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the correlation ID.
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the headers.
     */
    public void setHeaders(String headers) {
        this.headers = headers;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the message is ready for retry.
     */
    public boolean canRetry(int maxRetries) {
        return status != MessageStatus.DELIVERED && 
               status != MessageStatus.EXPIRED && 
               status != MessageStatus.REJECTED &&
               retryCount < maxRetries;
    }

    /**
     * Gets the processing duration in seconds.
     */
    public long getProcessingDuration() {
        if (sentAt == null || deliveredAt == null) {
            return 0;
        }
        return java.time.Duration.between(sentAt, deliveredAt).getSeconds();
    }

    // Getters
    public IntegrationId getIntegrationId() { return integrationId; }
    public String getMessageId() { return messageId; }
    public String getCorrelationId() { return correlationId; }
    public String getDirection() { return direction; }
    public MessageStatus getStatus() { return status; }
    public String getPayload() { return payload; }
    public String getHeaders() { return headers; }
    public String getEndpoint() { return endpoint; }
    public String getMethod() { return method; }
    public int getHttpStatus() { return httpStatus; }
    public int getRetryCount() { return retryCount; }
    public Instant getSentAt() { return sentAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public String getError() { return error; }
    public String getNotes() { return notes; }
    public boolean isProcessed() { return processed; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "IntegrationMessage{" +
                "id=" + getId() +
                ", messageId='" + messageId + '\'' +
                ", direction='" + direction + '\'' +
                ", status=" + status +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
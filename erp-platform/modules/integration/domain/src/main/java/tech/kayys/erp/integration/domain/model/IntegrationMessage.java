package tech.kayys.erp.integration.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.integration.domain.identifier.IntegrationId;
import tech.kayys.erp.integration.domain.identifier.MessageId;
import tech.kayys.erp.integration.domain.valueobject.MessageStatus;

import java.time.Instant;

/**
 * Integration message aggregate root.
 * Represents a message sent or received through an integration.
 */
public final class IntegrationMessage extends AggregateRoot<MessageId> {
    
    private static final long serialVersionUID = 1L;
    
    private IntegrationId integrationId;
    private String messageId;
    private String correlationId;
    private String direction;
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

    public static IntegrationMessage create(
            MessageId id,
            IntegrationId integrationId,
            String messageId,
            String direction,
            String payload) {
        IntegrationMessage msg = new IntegrationMessage(id);
        msg.integrationId = integrationId;
        msg.messageId = messageId;
        msg.direction = direction;
        msg.payload = payload;
        return msg;
    }

    public void send() {
        this.status = MessageStatus.SENT;
        this.sentAt = Instant.now();
        setUpdatedAt(Instant.now());
    }

    public void deliver() {
        this.status = MessageStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        this.processed = true;
        setUpdatedAt(Instant.now());
    }

    public void fail(String error) {
        this.status = MessageStatus.FAILED;
        this.error = error;
        setUpdatedAt(Instant.now());
    }

    public void retry() {
        this.retryCount++;
        this.status = MessageStatus.RETRY;
        setUpdatedAt(Instant.now());
    }

    public void process() {
        this.status = MessageStatus.PROCESSING;
        setUpdatedAt(Instant.now());
    }

    public boolean canRetry() {
        return status == MessageStatus.FAILED && retryCount < 3;
    }

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

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        setUpdatedAt(Instant.now());
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        setUpdatedAt(Instant.now());
    }

    public void setMethod(String method) {
        this.method = method;
        setUpdatedAt(Instant.now());
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
        setUpdatedAt(Instant.now());
    }

    public void setHeaders(String headers) {
        this.headers = headers;
        setUpdatedAt(Instant.now());
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
    }

    @Override
    public String toString() {
        return "IntegrationMessage{" +
                "id=" + getId() +
                ", messageId='" + messageId + '\'' +
                ", status=" + status +
                ", direction='" + direction + '\'' +
                '}';
    }
}

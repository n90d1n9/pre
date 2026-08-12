package tech.kayys.erp.billing.core.idempotency;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Idempotency key for billing operations.
 * Ensures that the same operation is not processed multiple times.
 */
public final class IdempotencyKey implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String key;
    private final String operationType;
    private final String resourceId;
    private final Instant createdAt;
    private final int ttlSeconds;

    public IdempotencyKey(String key, String operationType, String resourceId) {
        this(key, operationType, resourceId, 86400); // 24 hours default TTL
    }

    public IdempotencyKey(String key, String operationType, String resourceId, int ttlSeconds) {
        this.key = key;
        this.operationType = operationType;
        this.resourceId = resourceId;
        this.createdAt = Instant.now();
        this.ttlSeconds = ttlSeconds;
        validate();
    }

    @Override
    public void validate() {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Idempotency key cannot be empty");
        }
        if (operationType == null || operationType.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation type cannot be empty");
        }
        if (resourceId == null || resourceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource ID cannot be empty");
        }
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }
    }

    public String getKey() { return key; }
    public String getOperationType() { return operationType; }
    public String getResourceId() { return resourceId; }
    public Instant getCreatedAt() { return createdAt; }
    public int getTtlSeconds() { return ttlSeconds; }

    public boolean isExpired() {
        return Instant.now().isAfter(createdAt.plusSeconds(ttlSeconds));
    }

    public static IdempotencyKey generate(String operationType, String resourceId) {
        String key = operationType + ":" + resourceId + ":" + UUID.randomUUID().toString();
        return new IdempotencyKey(key, operationType, resourceId);
    }

    public static IdempotencyKey fromString(String key) {
        String[] parts = key.split(":");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid idempotency key format");
        }
        return new IdempotencyKey(key, parts[0], parts[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdempotencyKey that = (IdempotencyKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key;
    }
}
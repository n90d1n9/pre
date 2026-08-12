
import java.time.Instant;
import java.util.Objects;

/**
 * Audit trail entry.
 */
public final class AuditTrail implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String entityType;
    private final String entityId;
    private final String action;
    private final String fieldName;
    private final String oldValue;
    private final String newValue;
    private final String userId;
    private final String userIp;
    private final String userAgent;
    private final Instant timestamp;
    private final String notes;

    public AuditTrail(
            String entityType,
            String entityId,
            String action,
            String fieldName,
            String oldValue,
            String newValue,
            String userId,
            String userIp,
            String userAgent,
            Instant timestamp,
            String notes) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.userId = userId;
        this.userIp = userIp;
        this.userAgent = userAgent;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
        this.notes = notes;
        validate();
    }

    @Override
    public void validate() {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity type cannot be empty");
        }
        if (entityId == null || entityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity ID cannot be empty");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException("Action cannot be empty");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
    }

    // Getters
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getFieldName() { return fieldName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getUserId() { return userId; }
    public String getUserIp() { return userIp; }
    public String getUserAgent() { return userAgent; }
    public Instant getTimestamp() { return timestamp; }
    public String getNotes() { return notes; }

    public boolean isFieldChange() {
        return fieldName != null && !fieldName.trim().isEmpty() && 
               !Objects.equals(oldValue, newValue);
    }

    public boolean isCreate() {
        return "CREATE".equalsIgnoreCase(action);
    }

    public boolean isUpdate() {
        return "UPDATE".equalsIgnoreCase(action);
    }

    public boolean isDelete() {
        return "DELETE".equalsIgnoreCase(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditTrail that = (AuditTrail) o;
        return Objects.equals(entityType, that.entityType) &&
               Objects.equals(entityId, that.entityId) &&
               Objects.equals(action, that.action) &&
               Objects.equals(userId, that.userId) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType, entityId, action, userId, timestamp);
    }

    @Override
    public String toString() {
        return "AuditTrail{" +
                "entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", action='" + action + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String entityType;
        private String entityId;
        private String action;
        private String fieldName;
        private String oldValue;
        private String newValue;
        private String userId;
        private String userIp;
        private String userAgent;
        private Instant timestamp;
        private String notes;

        public Builder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder oldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder newValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userIp(String userIp) {
            this.userIp = userIp;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public AuditTrail build() {
            return new AuditTrail(
                entityType, entityId, action, fieldName, oldValue,
                newValue, userId, userIp, userAgent, timestamp, notes
            );
        }
    }
}
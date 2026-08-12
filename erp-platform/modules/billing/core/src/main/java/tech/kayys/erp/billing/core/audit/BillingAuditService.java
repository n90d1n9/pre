package tech.kayys.erp.billing.core.audit;

import tech.kayys.erp.billing.core.observability.BillingMetrics;
import tech.kayys.erp.billing.core.observability.BillingTracer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

/**
 * Billing audit service.
 * Tracks all changes to billing entities for compliance.
 */
@ApplicationScoped
public class BillingAuditService {

    @Inject
    AuditLogRepository auditLogRepository;

    @Inject
    BillingMetrics metrics;

    @Inject
    BillingTracer tracer;

    /**
     * Records an audit event.
     */
    public void recordAuditEvent(AuditEvent event) {
        try {
            auditLogRepository.save(event);
            metrics.recordEvent();
            tracer.recordEvent(
                tracer.startSpan("billing.audit"),
                event.getEventType(),
                event.getDetails()
            );
        } catch (Exception e) {
            // Log but don't fail - audit shouldn't break billing
            log.error("Failed to record audit event", e);
        }
    }

    /**
     * Records a billing action.
     */
    public void recordAction(
            String entityType,
            String entityId,
            String action,
            String performedBy,
            String details) {
        
        AuditEvent event = new AuditEvent(
            UUID.randomUUID().toString(),
            entityType,
            entityId,
            action,
            performedBy,
            details,
            Instant.now()
        );
        recordAuditEvent(event);
    }

    /**
     * Records a billing change.
     */
    public void recordChange(
            String entityType,
            String entityId,
            String changedBy,
            String fieldName,
            Object oldValue,
            Object newValue) {
        
        String details = String.format(
            "Changed %s from '%s' to '%s'",
            fieldName,
            oldValue != null ? oldValue.toString() : "null",
            newValue != null ? newValue.toString() : "null"
        );
        
        recordAction(entityType, entityId, "CHANGE", changedBy, details);
    }

    /**
     * Records a billing event.
     */
    public void recordEvent(
            String entityType,
            String entityId,
            String eventType,
            String triggeredBy,
            String details) {
        
        recordAction(entityType, entityId, eventType, triggeredBy, details);
    }

    /**
     * Audit event record.
     */
    public static final class AuditEvent {
        private final String id;
        private final String entityType;
        private final String entityId;
        private final String action;
        private final String performedBy;
        private final String details;
        private final Instant timestamp;

        public AuditEvent(
                String id,
                String entityType,
                String entityId,
                String action,
                String performedBy,
                String details,
                Instant timestamp) {
            this.id = id;
            this.entityType = entityType;
            this.entityId = entityId;
            this.action = action;
            this.performedBy = performedBy;
            this.details = details;
            this.timestamp = timestamp;
        }

        public String getId() { return id; }
        public String getEntityType() { return entityType; }
        public String getEntityId() { return entityId; }
        public String getAction() { return action; }
        public String getPerformedBy() { return performedBy; }
        public String getDetails() { return details; }
        public Instant getTimestamp() { return timestamp; }
    }
}
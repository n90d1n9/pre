package tech.kayys.erp.workflow.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Workflow instance identifier.
 */
public final class WorkflowInstanceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WorkflowInstanceId(UUID value) {
        super(value);
    }

    public static WorkflowInstanceId of(UUID value) {
        return new WorkflowInstanceId(value);
    }

    public static WorkflowInstanceId generate() {
        return new WorkflowInstanceId(UUID.randomUUID());
    }

    public static WorkflowInstanceId fromString(String value) {
        return new WorkflowInstanceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WorkflowInstanceId{" + value + "}";
    }
}
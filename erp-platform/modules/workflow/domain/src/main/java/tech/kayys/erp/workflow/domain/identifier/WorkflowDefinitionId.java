package tech.kayys.erp.workflow.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Workflow definition identifier.
 */
public final class WorkflowDefinitionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WorkflowDefinitionId(UUID value) {
        super(value);
    }

    public static WorkflowDefinitionId of(UUID value) {
        return new WorkflowDefinitionId(value);
    }

    public static WorkflowDefinitionId generate() {
        return new WorkflowDefinitionId(UUID.randomUUID());
    }

    public static WorkflowDefinitionId fromString(String value) {
        return new WorkflowDefinitionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WorkflowDefinitionId{" + value + "}";
    }
}
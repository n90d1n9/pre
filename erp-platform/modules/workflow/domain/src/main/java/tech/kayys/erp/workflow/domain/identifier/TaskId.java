package tech.kayys.erp.workflow.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Task identifier.
 */
public final class TaskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TaskId(UUID value) {
        super(value);
    }

    public static TaskId of(UUID value) {
        return new TaskId(value);
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId fromString(String value) {
        return new TaskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TaskId{" + value + "}";
    }
}
package tech.kayys.erp.project.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Milestone identifier.
 */
public final class MilestoneId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public MilestoneId(UUID value) {
        super(value);
    }

    public static MilestoneId of(UUID value) {
        return new MilestoneId(value);
    }

    public static MilestoneId generate() {
        return new MilestoneId(UUID.randomUUID());
    }

    public static MilestoneId fromString(String value) {
        return new MilestoneId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "MilestoneId{" + value + "}";
    }
}
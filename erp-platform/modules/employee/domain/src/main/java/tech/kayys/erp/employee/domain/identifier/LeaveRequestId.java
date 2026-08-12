package tech.kayys.erp.employee.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Leave request identifier.
 */
public final class LeaveRequestId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public LeaveRequestId(UUID value) {
        super(value);
    }

    public static LeaveRequestId of(UUID value) {
        return new LeaveRequestId(value);
    }

    public static LeaveRequestId generate() {
        return new LeaveRequestId(UUID.randomUUID());
    }

    public static LeaveRequestId fromString(String value) {
        return new LeaveRequestId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "LeaveRequestId{" + value + "}";
    }
}
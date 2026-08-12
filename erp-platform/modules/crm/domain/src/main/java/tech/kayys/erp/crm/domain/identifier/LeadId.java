package tech.kayys.erp.crm.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Lead identifier.
 */
public final class LeadId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public LeadId(UUID value) {
        super(value);
    }

    public static LeadId of(UUID value) {
        return new LeadId(value);
    }

    public static LeadId generate() {
        return new LeadId(UUID.randomUUID());
    }

    public static LeadId fromString(String value) {
        return new LeadId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "LeadId{" + value + "}";
    }
}
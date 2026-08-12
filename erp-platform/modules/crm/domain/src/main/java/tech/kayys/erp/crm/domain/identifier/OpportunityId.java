package tech.kayys.erp.crm.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Opportunity identifier.
 */
public final class OpportunityId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public OpportunityId(UUID value) {
        super(value);
    }

    public static OpportunityId of(UUID value) {
        return new OpportunityId(value);
    }

    public static OpportunityId generate() {
        return new OpportunityId(UUID.randomUUID());
    }

    public static OpportunityId fromString(String value) {
        return new OpportunityId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OpportunityId{" + value + "}";
    }
}
package tech.kayys.erp.billing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Billing schedule identifier.
 */
public final class BillingScheduleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public BillingScheduleId(UUID value) {
        super(value);
    }

    public static BillingScheduleId of(UUID value) {
        return new BillingScheduleId(value);
    }

    public static BillingScheduleId generate() {
        return new BillingScheduleId(UUID.randomUUID());
    }

    public static BillingScheduleId fromString(String value) {
        return new BillingScheduleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "BillingScheduleId{" + value + "}";
    }
}
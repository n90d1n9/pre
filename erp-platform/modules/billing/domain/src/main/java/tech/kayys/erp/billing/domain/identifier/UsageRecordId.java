package tech.kayys.erp.billing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class UsageRecordId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public UsageRecordId(UUID value) {
        super(value);
    }

    public static UsageRecordId of(UUID value) {
        return new UsageRecordId(value);
    }

    public static UsageRecordId generate() {
        return new UsageRecordId(UUID.randomUUID());
    }

    public static UsageRecordId fromString(String value) {
        return new UsageRecordId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "UsageRecordId{" + value + "}";
    }
}
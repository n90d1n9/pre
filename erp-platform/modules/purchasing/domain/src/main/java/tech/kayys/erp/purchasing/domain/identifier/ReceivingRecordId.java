package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class ReceivingRecordId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ReceivingRecordId(UUID value) {
        super(value);
    }

    public static ReceivingRecordId of(UUID value) {
        return new ReceivingRecordId(value);
    }

    public static ReceivingRecordId generate() {
        return new ReceivingRecordId(UUID.randomUUID());
    }

    public static ReceivingRecordId fromString(String value) {
        return new ReceivingRecordId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ReceivingRecordId{" + value + "}";
    }
}
package tech.kayys.erp.catalog.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class BundleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public BundleId(UUID value) {
        super(value);
    }

    public static BundleId of(UUID value) {
        return new BundleId(value);
    }

    public static BundleId generate() {
        return new BundleId(UUID.randomUUID());
    }

    public static BundleId fromString(String value) {
        return new BundleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "BundleId{" + value + "}";
    }
}
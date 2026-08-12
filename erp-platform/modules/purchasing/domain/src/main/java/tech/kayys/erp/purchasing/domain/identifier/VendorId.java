package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Vendor identifier in the Purchasing context.
 */
public final class VendorId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public VendorId(UUID value) {
        super(value);
    }

    public static VendorId of(UUID value) {
        return new VendorId(value);
    }

    public static VendorId generate() {
        return new VendorId(UUID.randomUUID());
    }

    public static VendorId fromString(String value) {
        return new VendorId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "VendorId{" + value + "}";
    }
}
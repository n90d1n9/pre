package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class VendorOnboardingId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public VendorOnboardingId(UUID value) {
        super(value);
    }

    public static VendorOnboardingId of(UUID value) {
        return new VendorOnboardingId(value);
    }

    public static VendorOnboardingId generate() {
        return new VendorOnboardingId(UUID.randomUUID());
    }

    public static VendorOnboardingId fromString(String value) {
        return new VendorOnboardingId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "VendorOnboardingId{" + value + "}";
    }
}
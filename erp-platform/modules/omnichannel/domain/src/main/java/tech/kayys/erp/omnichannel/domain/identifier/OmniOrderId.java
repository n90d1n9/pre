package tech.kayys.erp.omnichannel.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Omnichannel order identifier.
 */
public final class OmniOrderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public OmniOrderId(UUID value) {
        super(value);
    }

    public static OmniOrderId of(UUID value) {
        return new OmniOrderId(value);
    }

    public static OmniOrderId generate() {
        return new OmniOrderId(UUID.randomUUID());
    }

    public static OmniOrderId fromString(String value) {
        return new OmniOrderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OmniOrderId{" + value + "}";
    }
}
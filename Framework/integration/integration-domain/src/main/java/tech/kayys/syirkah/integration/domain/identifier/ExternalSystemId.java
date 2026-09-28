package tech.kayys.syirkah.integration.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Identifies a partner's system that Syirkah exchanges data with.
 */
public final class ExternalSystemId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public ExternalSystemId(UUID value) {
        super(value);
    }

    public static ExternalSystemId of(UUID value) {
        return new ExternalSystemId(value);
    }

    public static ExternalSystemId generate() {
        return new ExternalSystemId(UUID.randomUUID());
    }

    public static ExternalSystemId fromString(String value) {
        return new ExternalSystemId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ExternalSystemId{" + value + "}";
    }
}

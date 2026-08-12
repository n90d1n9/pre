package tech.kayys.erp.integration.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Integration identifier.
 */
public final class IntegrationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public IntegrationId(UUID value) {
        super(value);
    }

    public static IntegrationId of(UUID value) {
        return new IntegrationId(value);
    }

    public static IntegrationId generate() {
        return new IntegrationId(UUID.randomUUID());
    }

    public static IntegrationId fromString(String value) {
        return new IntegrationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "IntegrationId{" + value + "}";
    }
}
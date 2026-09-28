package tech.kayys.syirkah.security.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/** Identifies an authenticated principal: a user account or a service. */
public final class PrincipalId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public PrincipalId(UUID value) {
        super(value);
    }

    public static PrincipalId of(UUID value) {
        return new PrincipalId(value);
    }

    public static PrincipalId generate() {
        return new PrincipalId(UUID.randomUUID());
    }

    public static PrincipalId fromString(String value) {
        return new PrincipalId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PrincipalId{" + value + "}";
    }
}

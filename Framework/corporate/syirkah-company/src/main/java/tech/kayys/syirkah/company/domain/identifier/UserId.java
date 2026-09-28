package tech.kayys.syirkah.company.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * User identifier.
 */
public final class UserId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public UserId(UUID value) {
        super(value);
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromString(String value) {
        return new UserId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "UserId{" + value + "}";
    }
}

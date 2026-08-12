package tech.kayys.erp.catalog.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class ReviewId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ReviewId(UUID value) {
        super(value);
    }

    public static ReviewId of(UUID value) {
        return new ReviewId(value);
    }

    public static ReviewId generate() {
        return new ReviewId(UUID.randomUUID());
    }

    public static ReviewId fromString(String value) {
        return new ReviewId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ReviewId{" + value + "}";
    }
}
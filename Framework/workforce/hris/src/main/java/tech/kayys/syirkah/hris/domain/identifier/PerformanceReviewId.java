package tech.kayys.syirkah.hris.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Performance review identifier.
 */
public final class PerformanceReviewId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public PerformanceReviewId(UUID value) {
        super(value);
    }

    public static PerformanceReviewId of(UUID value) {
        return new PerformanceReviewId(value);
    }

    public static PerformanceReviewId generate() {
        return new PerformanceReviewId(UUID.randomUUID());
    }

    public static PerformanceReviewId fromString(String value) {
        return new PerformanceReviewId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PerformanceReviewId{" + value + "}";
    }
}

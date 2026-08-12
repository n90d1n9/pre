package tech.kayys.erp.analytics.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Report identifier.
 */
public final class ReportId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ReportId(UUID value) {
        super(value);
    }

    public static ReportId of(UUID value) {
        return new ReportId(value);
    }

    public static ReportId generate() {
        return new ReportId(UUID.randomUUID());
    }

    public static ReportId fromString(String value) {
        return new ReportId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ReportId{" + value + "}";
    }
}
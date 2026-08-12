package tech.kayys.erp.company.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Company identifier.
 */
public final class CompanyId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CompanyId(UUID value) {
        super(value);
    }

    public static CompanyId of(UUID value) {
        return new CompanyId(value);
    }

    public static CompanyId generate() {
        return new CompanyId(UUID.randomUUID());
    }

    public static CompanyId fromString(String value) {
        return new CompanyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CompanyId{" + value + "}";
    }
}
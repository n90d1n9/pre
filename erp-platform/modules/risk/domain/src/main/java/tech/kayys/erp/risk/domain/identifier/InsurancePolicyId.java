package tech.kayys.erp.risk.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Insurance policy identifier.
 */
public final class InsurancePolicyId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InsurancePolicyId(UUID value) {
        super(value);
    }

    public static InsurancePolicyId of(UUID value) {
        return new InsurancePolicyId(value);
    }

    public static InsurancePolicyId generate() {
        return new InsurancePolicyId(UUID.randomUUID());
    }

    public static InsurancePolicyId fromString(String value) {
        return new InsurancePolicyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InsurancePolicyId{" + value + "}";
    }
}
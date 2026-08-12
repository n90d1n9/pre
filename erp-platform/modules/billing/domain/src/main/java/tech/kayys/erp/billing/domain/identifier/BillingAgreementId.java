package tech.kayys.erp.billing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class BillingAgreementId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public BillingAgreementId(UUID value) {
        super(value);
    }

    public static BillingAgreementId of(UUID value) {
        return new BillingAgreementId(value);
    }

    public static BillingAgreementId generate() {
        return new BillingAgreementId(UUID.randomUUID());
    }

    public static BillingAgreementId fromString(String value) {
        return new BillingAgreementId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "BillingAgreementId{" + value + "}";
    }
}